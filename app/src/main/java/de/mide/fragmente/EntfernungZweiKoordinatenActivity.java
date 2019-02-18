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
 * Activitiy für die Berechnung der Entfernung zwischen zwei Koordinaten,
 * die mit zwei Fragment-Instanzen eingegeben werden.
 * <br><br>
 *
 * This file is licensed under the terms of the BSD 3-Clause License.
 */
public class EntfernungZweiKoordinatenActivity extends FragmentActivity implements OnClickListener {

    /** Button zur Durchführung der eigentlichen Berechnung. */
	protected Button _berechnungDurchfuehrenButton = null;

	/** Fragment für Eingabe der ersten Koodinate. */
	protected KoordinatenFragment _koordinatenFragment1 = null;

    /** Fragment für Eingabe der zweiten Koodinate. */
	protected KoordinatenFragment _koordinatenFragment2 = null;
	
	
	/**
	 * Lifecycle-Methode: Layout-Datei laden, Referenzen auf UI-Elemente holen.
	 * Auch Referenzen auf Fragments holen.
	 */
	@Override
	protected void onCreate(Bundle bundle) {

		super.onCreate(bundle);
		
		setContentView(R.layout.activity_entfernung_zwei_koordinaten);

		// Button initialisieren
		_berechnungDurchfuehrenButton = findViewById(R.id.button_berechne_entfernung_zwischen_zwei_koordinaten);
		_berechnungDurchfuehrenButton.setOnClickListener(this);
		
		// Achtung: FragmentManager aus Paket "android.support.v4.app", nicht den aus Paket "android.app" !!!
		FragmentManager fragmentManager = getSupportFragmentManager();
		_koordinatenFragment1 = (KoordinatenFragment) fragmentManager.findFragmentById(R.id.fragment_koordinate_1);
		_koordinatenFragment2 = (KoordinatenFragment) fragmentManager.findFragmentById(R.id.fragment_koordinate_2);
	}

	
	/**
	 * Event-Handler für Buttons.
	 * 
	 * @param view UI-Element, das Ereignis ausgelöst hat.
	 */
	@Override
	public void onClick(View view) {

		if (view == _berechnungDurchfuehrenButton) {
			
			Location location1 = _koordinatenFragment1.getLocation();
			if (location1 == null) return;
			
			Location location2 = _koordinatenFragment2.getLocation();
			if (location2 == null) return;
			
			double distanzMeter = location1.distanceTo(location2);
			
			if (distanzMeter < 1000) {
				long distanzGerundet = Math.round(distanzMeter);
				zeigeToast("Entfernung: " + distanzGerundet + " m");
			}
			else {
				long distanzGerundet = Math.round(distanzMeter/1000.0);
				zeigeToast("Entfernung: " + distanzGerundet + " km");
			}			
			
		} else
			Log.w(MainActivity.TAG4LOGGING, "Unerwartetes UI-Element hat Event-Handler aufgerufen: " + view);
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
