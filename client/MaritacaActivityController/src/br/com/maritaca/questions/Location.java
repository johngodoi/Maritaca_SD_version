package br.com.maritaca.questions;

import org.w3c.dom.Element;

import android.content.Context;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import br.com.maritaca.MaritacaActivityController;
import br.com.maritaca.xml.parser.XMLParser;

public class Location extends Question {

	private boolean show;
	private LocationManager locationManager;

	public Location(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element) {
		super(id, previous, next, help, label, required, element);
		// TODO Auto-generated constructor stub
		this.show = XMLParser.myParseBoolean(element.getAttribute("show"));
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
		return false;
	}

	@Override
	public void save(View answer) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getLayout(final MaritacaActivityController controller) {
		android.location.Location location = null;
		locationManager = (LocationManager) controller
				.getSystemService(Context.LOCATION_SERVICE);
		boolean isGpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (isGpsEnabled) {
			location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		} else {
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		if (location != null) {
			int lat = (int) (location.getLatitude());
			int lon = (int) (location.getLongitude());
			value = String.valueOf(lat) + "," + String.valueOf(lon);
		} else {
			value = "Não foi possível obter a localizacao";
		}
		if (show) {
			EditText campoDeResposta = new EditText(controller);
			campoDeResposta.setText(value.toString());
			return campoDeResposta;
		}
		Button button = new Button(controller);
		button.setText("Obter data");
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Toast.makeText(controller, value.toString(), Toast.LENGTH_LONG)
						.show();
			}
		});
		return button;

	}

}
