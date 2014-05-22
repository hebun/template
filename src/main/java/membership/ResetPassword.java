package membership;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.validation.constraints.Size;

import freela.util.App;
import freela.util.BaseBean;
import freela.util.Db;
import freela.util.FaceUtils;
import freela.util.Sql;

@ViewScoped
@ManagedBean
public class ResetPassword extends BaseBean implements Serializable {

	@Size(min = 4, max = 16, message = "{lengthNotValid}")
	String newPassword;

	String reNewPassword;

	@ManagedProperty(value = "#{app}")
	App app;

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public String resetPassword() {
		if (!newPassword.equals(reNewPassword)) {
			FaceUtils.addError("Şifreler aynı değil");
			return null;
		}

		Db.update(new Sql.Update("user").add("password", newPassword)
				.where("id", this.data.get(0).get("userid")).get());
		
		app.setCurrentInfoMessage("Şifreniz güncellendi. Şimdi giriş yapabilirsiniz.");

		return "bilgi";
	}

	public ResetPassword() {

		this.data = null;

		this.table = "resetpassword";

		String code = FaceUtils.getGET("code");
		if (code == null) {
			return;
		}
		this.loadData("code", code);

		if (this.data == null) {
			return;
		}
		if (this.data.size() == 0) {
			this.data = null;
		}

	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getReNewPassword() {
		return reNewPassword;
	}

	public void setReNewPassword(String reNewPassword) {
		this.reNewPassword = reNewPassword;
	}

	private static final long serialVersionUID = 1L;

}
