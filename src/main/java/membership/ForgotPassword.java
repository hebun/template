package membership;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.mail.MessagingException;

import freela.util.App;
import freela.util.Db;
import freela.util.FaceUtils;
import freela.util.Sql;

@ViewScoped
@ManagedBean
public class ForgotPassword {
	private static final String FAZLASTOKLAR_COM = "mydomain.com";
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@ManagedProperty(value = "#{app}")
	App app;
	
	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public String forgotPassword() {

		List<Map<String, String>> userTable = Db.selectTable(new Sql.Select()
				.from("user").where("email", username).and("state", "ACTIVE")
				.get());

		if (userTable.size() == 0) {
			FaceUtils
					.addError("Bu E-Mail adresi ile kayıtlı kullanıcı bulunamadı.");
			return null;
		}

		List<Map<String, String>> table = Db.selectTable(new Sql.Select()
				.from("mailcontent").where("name", "resetpassword").get());

		String mc = table.get(0).get("content");

		String uid = UUID.randomUUID().toString();

		Db.insert(new Sql.Insert("resetpassword").add("code", uid)
				.add("userid", userTable.get(0).get("id"))
				.add("tarih", FaceUtils.getFormattedTime())
				.get() );

		mc = mc.replaceAll("#link#", FaceUtils.getRootUrl()
				+ "/resetpassword?code=" + uid);

		try {
			FaceUtils.postMail(new String[] { "ismettung@gmail.com", username },
					FAZLASTOKLAR_COM + " Şifre Yenile", mc, "");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		app.setCurrentInfoMessage("Ayrıntılı talimatlar E-Posta adresinize gönderildi");

		return "bilgi";
	}
}
