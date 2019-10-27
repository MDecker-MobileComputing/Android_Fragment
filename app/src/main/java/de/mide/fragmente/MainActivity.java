package de.mide.fragmente;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


/**
 * Demo für Verwendung von Fragmenten.
 * <br><br>
 *
 * This file is licensed under the terms of the BSD 3-Clause License.
 */
public class MainActivity extends Activity implements OnClickListener {

	public static String TAG4LOGGING = "Fragmente_Demo";

	/** Button für Öffnen der Activity zur Berechnung der Entfernung nach Berlin. */
	protected Button _berechneEntfernungNachBerlinButton = null;

    /** Button für Öffnen der Activity zur Berechnung der Entfernung zwischen zwei Orten. */
	protected Button _berechneEntfernungZwischenZweiKoordinatenButton = null;


	/**
	 * Lifecycle-Methode, lädt Layout-Datei und setzt Event-Handler für Buttons.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_berechneEntfernungNachBerlinButton = findViewById(R.id.button_entfernung_zu_berlin);
		_berechneEntfernungNachBerlinButton.setOnClickListener(this);

		_berechneEntfernungZwischenZweiKoordinatenButton = findViewById(R.id.button_entfernung_zwischen_zwei_orten);
		_berechneEntfernungZwischenZweiKoordinatenButton.setOnClickListener(this);
	}


	/**
	 * Event-Handler für Button, einzige Methode aus Interface {@link OnClickListener}.
	 *
	 * @param view Objekt, das das Event ausgelöst hat (sollte ein Button sein).
	 */
	@Override
	public void onClick(View view) {

		Intent intent = null;

		if (view == _berechneEntfernungNachBerlinButton) {

			intent = new Intent(this, EntfernungVonBerlinActivity.class);
			startActivity(intent);

		} else if (view == _berechneEntfernungZwischenZweiKoordinatenButton) {

			intent = new Intent(this, EntfernungZweiKoordinatenActivity.class);
			startActivity(intent);

		} else {

			Log.w(TAG4LOGGING, "Unerwartetes UI-Element hat Event-Handler ausgelöst: " + view);
		}
	}

};
