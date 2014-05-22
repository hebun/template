package upload;

import static freela.util.FaceUtils.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.Part;

import freela.util.BaseBean;
import freela.util.FaceUtils;

@ManagedBean
@ViewScoped
public class Upload extends BaseBean {
	private static final String SIZE_LIMIT_STRING = "1 MB";
	private static final int MAX_IMAGE_COUNT = 8;
	private static final int SIZE_LIMIT = 1_000_000;
	Part file;

	public Upload() {
		this.record = new HashMap<String, String>();
		this.data = new ArrayList<>();
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	public boolean validateFile() {

		boolean fail = false;
		String msg = null;
		if (file.getSize() > SIZE_LIMIT) {
			msg = ("En fazla " + SIZE_LIMIT_STRING + " boyuntunda dosya yüklenebilir.");
			fail = true;
		}
		if (!"image".equals(file.getContentType().split("/")[0])) {
			msg = ("Sadece resim dosyaları yüklenebilir.");
			fail = true;
		}
		if (data.size() >= MAX_IMAGE_COUNT) {
			msg = ("En fazla " + MAX_IMAGE_COUNT + " resim yüklenebilir.");
			fail = true;
		}

		if (fail) {

			FaceUtils.addError(msg);
		}
		log.info(fail + "");
		return fail;

	}

	public String upload() {

		try {
			if (file == null)
				return "";

			if (validateFile())
				return null;

			try {
				final File filex = File.createTempFile("pre", FaceUtils
						.getFilename(file), new File(FaceUtils.uploadDir));

				String absolutePath = filex.getAbsolutePath();
				file.write(absolutePath);
				this.record.put("file", filex.getName());
				this.data.add(this.record);
				file = null;
			} catch (IOException e) {
				log.warning(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String deletePhoto(Map<String, String> rec) {

		data.remove(rec);

		try {
			Files.deleteIfExists(Paths.get(FaceUtils.uploadDir
					+ rec.get("file")));
		} catch (IOException e) {
			FaceUtils.log.warning(e.getMessage());

		}
		return null;

	}
}
