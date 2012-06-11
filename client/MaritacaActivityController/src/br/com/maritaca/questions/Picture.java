package br.com.maritaca.questions;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.w3c.dom.Element;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import br.com.maritaca.MaritacaActivityController;

public class Picture extends Question {

	private String fileName = String.valueOf(System.currentTimeMillis());
	private String filePath = null;

	public Picture(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element) {
		super(id, previous, next, help, label, required, element);
		// TODO Auto-generated constructor stub
		Log.d("PICTURE", "" + next);
	}

	@Override
	public Object getValue() {

		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public void setValue(Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return value != null;
	}

	@Override
	public void save(View answer) {
		try {

			Bitmap bitmap = BitmapFactory.decodeFile(filePath);
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bStream);
			value = Base64
					.encodeToString(bStream.toByteArray(), Base64.DEFAULT);
			bStream.close();
			Log.d("PICTURE", value.toString());
			// BufferedImage img = ImageIO.read(new File(filePath));
			// ByteArrayOutputStream byteArrayOutputStream = new
			// ByteArrayOutputStream();
			// ImageIO.write(img, "jpg", byteArrayOutputStream);
			// byteArrayOutputStream.flush();
			// value =
			// base64.encodeToString(byteArrayOutputStream.toByteArray());
			// byteArrayOutputStream.close();
		} catch (Exception e) {
			Toast.makeText(answer.getContext(),
					"Erro durante a codificacao da imagem", Toast.LENGTH_LONG)
					.show();
		}

	}

	@Override
	public View getLayout(final MaritacaActivityController controller) {
		Button button = new Button(controller.getApplicationContext());
		button.setText("Picture");
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				File image = new File(MaritacaActivityController.diretorio,
						fileName);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				filePath = image.getAbsolutePath();

				controller
						.startActivityForResult(
								intent,
								MaritacaActivityController.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});
		return button;
	}

}
