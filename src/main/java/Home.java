import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import upload.Upload;

@ManagedBean
@ViewScoped
public class Home {

	@ManagedProperty(value = "#{upload}")
	Upload upload;

	public Upload getUpload() {
		return upload;
	}

	public void setUpload(Upload upload) {
		this.upload = upload;
	}

	public Home() {
	}

	@PostConstruct
	public void init() {
	//	upload.setData(new Select("file").from("productphoto").limit(3).getTable());
	}
}
