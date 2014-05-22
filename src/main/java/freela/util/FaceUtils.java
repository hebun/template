package freela.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import freela.util.Sql.Select;

public class FaceUtils {
	public static final Logger log = Logger.getLogger("template");

	public static String SMTP_HOST_NAME = "localhost"; // or

	public static String SMTP_AUTH_USER = "";
	public static String SMTP_AUTH_PWD = "";

	public static String emailFromAddress = "";

	public static String uploadDir;
	static {
		log.setUseParentHandlers(false);
		
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new Formatter() {

			private final DateFormat df = new SimpleDateFormat(
					"hh:mm:ss dd/MM/yyyy ");

			@Override
			public String format(LogRecord record) {
				StackTraceElement[] stackTrace = Thread.currentThread()
						.getStackTrace();
				String caller = stackTrace[9].getMethodName();
				StringBuilder builder = new StringBuilder(1000);
				builder.append("[").append(record.getLevel()).append("] ");
				builder.append(formatMessage(record));

				builder.append("    [" + record.getSourceMethodName())
						.append("]<-").append(caller).append(" - ");
				builder.append(df.format(new Date(record.getMillis()))).append(
						" - ");
				builder.append("[").append(record.getSourceClassName())
						.append(".").append(record.getSourceMethodName())
						.append("]");

				builder.append("\n");

				return builder.toString();
			}

		});
		consoleHandler.setLevel(Level.ALL);

		if (log.getHandlers().length == 0) {
			log.addHandler(consoleHandler);
		}

		log.setLevel(Level.ALL);

	}

	public static String getRootUrl() {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		String contextURL = request.getRequestURL().toString()
				.replace(request.getRequestURI().substring(0), "")
				+ request.getContextPath();
		return contextURL;
	}

	public static String getFilename(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim()
						.replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1)
						.substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
			}
		}
		return null;
	}

	public static <T> T getObjectFromGETParam(String param, Class<T> type,
			String table) {

		String string = getGET(param);
		if (string != null)
			return getObjectById(type, table, string);
		else {
			try {
				return type.newInstance();
			} catch (InstantiationException e) {
				log.warning(e.getMessage());
			} catch (IllegalAccessException e) {
				log.warning(e.getMessage());
			}
		}
		return null;

	}

	public static <T> T getObjectById(Class<T> type, String table, String id) {

		Sql.Select sql = new Sql.Select().from(table).where("id=", id)
				;

		List<T> li = Db.preparedSelect(sql.get(), sql.params(), type);
		T ret = null;
		try {
			if (li.size() == 0) {

				ret = type.newInstance();
			} else {
				ret = li.get(0);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String getGET(String param) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext context = facesContext.getExternalContext();
		String string = context.getRequestParameterMap().get(param);
		return string;
	}

	public static void addError(String msg) {
		addError(null, msg);
	}

	public static void addError(String id, String msg) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
				msg, msg);
		FacesContext.getCurrentInstance().addMessage(id, message);
	}

	public static void redirectTo(String url) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext context = facesContext.getExternalContext();
		HttpServletResponse response = (HttpServletResponse) context
				.getResponse();

		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			log.warning(e.getMessage());
			e.printStackTrace();
		}
	}

	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {

				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static void addCookie(String name, String value, int maxAge) {
		addCookie((HttpServletResponse) FacesContext.getCurrentInstance()
				.getExternalContext().getResponse(), name, value, maxAge);

	}

	public static void addCookie(HttpServletResponse response, String name,
			String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public static void removeCookie(HttpServletResponse response, String name) {
		addCookie(response, name, null, 0);
	}


	public static String getFormattedTime() {
		Date time = Calendar.getInstance().getTime();
		return getFormattedTime(time);

	}

	public static String getFormattedTime(Date time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("Y-M-d H:m:s");

		String formattedTime = dateFormat.format(time);
		return formattedTime;
	}

	public static void postMail(String recipients[], String subject,
			String message, String from) throws MessagingException,
			AuthenticationFailedException {

		boolean debug = false;
		from = emailFromAddress;
		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		Authenticator auth = new SMTPAuthenticator();
		Session session = Session.getDefaultInstance(props, auth);

		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, "text/html; charset=utf-8");
		Transport.send(msg);
	}

	public static class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			String username = SMTP_AUTH_USER;
			String password = SMTP_AUTH_PWD;
			return new PasswordAuthentication(username, password);
		}
	}
}
