package de.mide.fragmente;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Diese Activity dient zur Berechnung der Entfernung der mit EINER Instanz
 * des Fragments eingegebenen Koordinate zu Berlin.<br><br>
 *
 * This file is licensed under the terms of the BSD 3-Clause License.
 */
public class EntfernungVonBerlinActivity extends FragmentActivity implements OnClickListener {

    /** Fragment zur Eingabe einer geographischen Koordinate. */
	protected KoordinatenFragment _koordinatenFragment = null;

	/** Button zum Start der Berechnung. */
	protected Button _berechnungDurchfuehrenButton = null;

    /** Objekt mit den Koordinaten von Berlin. */
	protected Location _berlinLocation = null;


	/**
	 * Lifecycle-Methoden, wie üblich zum Layout-Datei zu Laden und UI-Elemente
	 * wie Buttons zu initialisieren. Es wird jetzt auch über den Fragment-Manager
	 * die Referenz auf das in der Layout-Datei verwendete Fragment-Objekt ausgelesen.
	 */
	@Override
	protected void onCreate(Bundle bundle) {

		super.onCreate(bundle);

		setContentView(R.layout.activity_entfernung_von_berlin);


		// Achtung: FragmentManager aus Paket "android.support.v4.app", nicht den aus Paket "android.app" !!!
		FragmentManager fragmentManager = getSupportFragmentManager();
		_koordinatenFragment = (KoordinatenFragment) fragmentManager.findFragmentById(R.id.fragment_koordinate);


		_berechnungDurchfuehrenButton = findViewById(R.id.button_berechne_entfernung_zu_berlin);
		_berechnungDurchfuehrenButton.setOnClickListener(this);


		// Location-Objekt mit Koordinaten von Berlin erzeugen (52° 31'7" North, 13° 24' 29" East)
		_berlinLocation = new Location("dummy-provider");
		_berlinLocation.setLongitude( 13.408056 ); // östlich
		_berlinLocation.setLatitude ( 52.518611 ); // nördlich
	}


	/**
	 * Event-Handler für Button.
	 *
	 * @param view UI-Element (Button), welches das Event ausgelöst hat.
	 */
	@Override
	public void onClick(View view) {

		if (view == _berechnungDurchfuehrenButton) {

			Location locationEingegeben = _koordinatenFragment.getLocation();
			if (locationEingegeben == null) { return; } // noch keine zulässige Koordinate eingegeben

			// Eigentliche Entfernungs-Berechnung durchführen
			double distanzMeter = locationEingegeben.distanceTo(_berlinLocation);

			if (distanzMeter <= 1000) {

				long distanzGerundet = Math.round(distanzMeter);
				zeigeToast("Entfernung nach Berlin: " + distanzGerundet + " m");
			}
			else {

				long distanzGerundet = Math.round(distanzMeter/1000.0);
				zeigeToast("Entfernung nach Berlin: " + distanzGerundet + " km");
			}

		} else {

			Log.w(MainActivity.TAG4LOGGING,
				  "Unerwartetes UI-Element hat Event-Handler-Methode in EntfernungVonBerlinActivity aufgerufen: " + view);
        }
	}


	/**
	 * Nachricht mit Toast-Objekt (lange Dauer) ausgeben.
	 *
	 * @param nachricht Text, der mit dem Toast auszugeben ist.
	 */
	protected void zeigeToast(String nachricht) {

		Toast toast = Toast.makeText(this, nachricht, Toast.LENGTH_LONG);
		toast.show();
	}

};
