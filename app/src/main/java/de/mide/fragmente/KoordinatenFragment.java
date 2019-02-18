package de.mide.fragmente;

import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


/**
 * Fragment-Klasse für die Eingabe einer Koordinate.
 * <br><br>
 *
 * This file is licensed under the terms of the BSD 3-Clause License.
 */
public class KoordinatenFragment extends Fragment implements OnClickListener {
	
	/** Eigener Aufzählungstyp für die vier Himmelsrichtungen. */
	public enum HimmelsrichtungEnum { NORDEN, OSTEN, SUEDEN, WESTEN, UNBEKANNT };

	protected static final String TAG4LOGGING = "KoordinatenFragment";
		
	protected RadioGroup _ostOderWestRadioGroup  = null;
	protected RadioGroup _nordOderSuedRadioGroup = null;
	
	protected RadioButton _ostRadioButton  = null;
	protected RadioButton _westRadioButton = null;
	protected RadioButton _nordRadioButton = null;
	protected RadioButton _suedRadioButton = null;
	
	/** UI-Element zur Eingabe der geographischen Länge. */
	protected EditText _geoLaengeEditText = null;

        /** UI-Element zur Eingabe der geographischen Breite. */
	protected EditText _geoBreiteEditText = null;
	
	protected Button _resetButton         = null;
	protected Button _kartenansichtButton = null;
	protected Button _zufallsButton       = null;

	
	/** 
	 * Layout-Datei für Fragment mit Inflater "aufblasen" und View daraus erzeugen.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, 
                             ViewGroup      container,
                             Bundle         savedInstanceState) {
						
		return inflater.inflate(R.layout.fragment_geokoordinaten, container, false);		
	}

	
	/**
	 * Referenzen auf einzelne UI-Elemente holen und in Member-Variablen
	 * speichern. Es wird auch die Fragment-Instanz als Event-Listener
	 * für die Buttons registriert.
	 * 
	 * @param view Referenz auf View-Objekt, das von Methode <i>onCreateView()</i>
	 *             mit Inflater erstellt und mit return zurückgegeben wurde.
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
		
		// Referenzen von Buttons holen und auch Event-Handler setzen
		_resetButton = view.findViewById(R.id.button_reset_koordinate);
		_resetButton.setOnClickListener(this);
		
		_kartenansichtButton = view.findViewById(R.id.button_kartenansicht);
		_kartenansichtButton.setOnClickListener(this);
		
		_zufallsButton  = view.findViewById(R.id.button_koordinate_zufall);
		_zufallsButton.setOnClickListener(this);
		
		
		// Referenzen auf EditText-Elemente für Eingabe der Dezimalwerte holen
		_geoLaengeEditText = view.findViewById(R.id.edittext_geograph_laenge);
		_geoBreiteEditText = view.findViewById(R.id.edittext_geograph_breite);
		
		
		// Referenzen von RadioGrups & RadioButtons holen
		_ostOderWestRadioGroup  = view.findViewById(R.id.radioButtonGruppeOstOderWest );
		_nordOderSuedRadioGroup = view.findViewById(R.id.radioButtonGruppeNordOderSued);
		
		_ostRadioButton  = view.findViewById(R.id.radio_ost );
		_westRadioButton = view.findViewById(R.id.radio_west);
		_nordRadioButton = view.findViewById(R.id.radio_nord);
		_suedRadioButton = view.findViewById(R.id.radio_sued);
	}


	/**
	 * Einzige Methode aus Interface {@link OnClickListener},
	 * EventHandler für Buttons.
	 * 
	 * @param view UI-Element, welches das Event ausgelöst hat (sollte Button sein).
	 */
	@Override
	public void onClick(View view) {

		if (view == _resetButton) {

			_geoLaengeEditText.setText("");
			_geoBreiteEditText.setText("");
			
		} else if (view == _kartenansichtButton) {
						 
			oeffneKartenansicht();
			
		} else if (view == _zufallsButton) {
			
			setzeZufallsKoordinaten();
			
		} else {

			Log.w(TAG4LOGGING, "Event-Handler von unerwartetem UI-Element ausgelöst: " + view);
		}
	}

	
	/**
	 * Methode gibt nur dann {@code true} zurück, wenn aktuell
	 * gültige Koordinaten eingegeben sind.
	 * überprüft z.B., ob für geographische Länge eingegebener Wert
	 * tatsächlich im Bereich von 0 und 180 ist.
	 * 
	 * Wenn etwas nicht stimmt, dann wird dies in einem Toast ausgegeben
	 * (der Toast zeigt aber nur den ersten gefundenen Fehler an).
	 */
	protected boolean checkKoordinaten() {
		
		// *** Überprüfungen für geog. Länge ***
		
		String geoKoordStr = _geoLaengeEditText.getText().toString().trim();
		if (geoKoordStr.length() == 0) {
			zeigeToast("Kein Wert für geographische Länge eingegeben.");
			return false;
		}
		
		// Es kann davon ausgegangen werden, dass eine zulässige
		// Dezimalzahl eingegeben wurde, weil für das EditText-Element
		// das folgende Attribut gesetzt wurde: android:inputType="numberDecimal" 
		double geoLaenge = Double.parseDouble(geoKoordStr);
		
		if (geoLaenge < 0.0 || geoLaenge > 180.0) {
			zeigeToast("Wert für Länge \"" + geoKoordStr + "\" ist nicht im Wertebereich von 0..180 Grad.");
			return false;			
		}
		
		if (getOstOderWest() == HimmelsrichtungEnum.UNBEKANNT) {
			zeigeToast("Bitte entweder \"Ost\" oder \"West\" für geographische Länge wählen.");
			return false;
		}
		
		
		// *** Überprüfungen für geog. Breite ***

		geoKoordStr = _geoBreiteEditText.getText().toString().trim();
		if (geoKoordStr.length() == 0) {
			zeigeToast("Kein Wert für geographische Breite eingegeben.");
			return false;
		}		
		
		double geoBreite = Double.parseDouble(geoKoordStr);
		
		if (geoBreite  < 0.0 || geoBreite  > 90.0) {
			zeigeToast("Wert für Breite \"" + geoKoordStr + "\" ist nicht im Wertebereich von 0..90 Grad.");
			return false;			
		}
		
		if (getNordOderSued() == HimmelsrichtungEnum.UNBEKANNT) {
			zeigeToast("Bitte entweder \"Nord\" oder \"Süd\" für geographische Breite wählen.");
			return false;
		}		
		

		// Wenn wir bisher gekommen sind, dann wurden alle Tests bestanden und die Eingabe ist zulässig
		return true;  
	}

	
	/**
	 * Methode versucht aktuellen Koordinaten in einer Karten-App
	 * (z.B. GoogleMaps) per impliziten Intent zu öffnen.
	 * Es wird überprüft, ob eine Karten-App zur Verfügung steht; wenn nicht,
	 * dann wird eine Fehlermeldung per Toast ausgegeben und der entsprechende
	 * Button deaktiviert. Siehe hierzu auch Beispiel-App "Android_ImplizitIntentDemo".
	 */
	protected void oeffneKartenansicht() {

		if (checkKoordinaten() == false) { return; } // wenn aktuell keine zulässigen Koordinaten eingegeben sind, dann brechen wir ab
				
		// URI mit darzustellender Koordinate bilden, z.B. "geo:49.014,8.4043"
		String uriString = "geo:" + _geoBreiteEditText.getText() + "," + _geoLaengeEditText.getText();
		
		Uri    uri    = Uri.parse(uriString); 
		Intent intent = new Intent(Intent.ACTION_VIEW);				
		intent.setData(uri);
		
		
		// Überprüfen, ob auf dem Android-Gerät überhaupt eine Karten-App zur Verfügung steht.
		if ( wirdIntentUnterstuetzt(intent) ) {
			
			startActivity(intent);
			
		} else {

			_kartenansichtButton.setEnabled(false);
			zeigeToast("Die Koordinate kann leider nicht dargestellt werden, weil keine Karten-App zur Verfügung steht.");
		}
	}
	
	
	/**
	 * Prüfen, ob es für den als Parameter übergebenen Intent mindestens
	 * eine passende Zielkomponente gibt. 
	 * 
	 * @param intent Intent, für den überprüft werden soll, ob er
	 *               vom aktuellen Android-System ausgeführt werden kann
	 *               (d.h. es gibt mindestens eine App/Komponente,
	 *                die ihn verarbeiten kann).
	 *                
	 * @return {@code true}, wenn es mindestens einen Receiver gibt,
	 *         der Intent also unterstützt wird.
	 */
	protected boolean wirdIntentUnterstuetzt(Intent intent) {
		
		final PackageManager packageManager = getActivity().getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities( 
									intent, 
									PackageManager.MATCH_DEFAULT_ONLY);
		
		return list.size() > 0; // true, wenn mindestens eine Komponente für den Inten gefunden wurde
	}	

	
	/**
	 * Abfragen der Himmelsrichtung für geogr. Länge.
	 * 
	 * @return Ost, West oder Unbekannt (wenn noch nichts ausgewählt).
	 */
	protected HimmelsrichtungEnum getOstOderWest() {
		
		int idOfSelectedButton = _ostOderWestRadioGroup.getCheckedRadioButtonId();
		
		
		if (idOfSelectedButton == -1) return HimmelsrichtungEnum.UNBEKANNT;
		
		if (idOfSelectedButton == _ostRadioButton.getId()) return HimmelsrichtungEnum.OSTEN;
		
		if (idOfSelectedButton == _westRadioButton.getId()) return HimmelsrichtungEnum.WESTEN;
				
		Log.e(TAG4LOGGING, "Unerwarteter RadioButton für Ost/West gewählt");
		
		return HimmelsrichtungEnum.UNBEKANNT;
	}
	
	
	/**
	 * Abfragen der Himmelsrichtung für die geograpische Breite.
	 * 
	 * @return Nord, Süd oder Unbekannt (wenn noch nichts ausgewählt).
	 */
	public HimmelsrichtungEnum getNordOderSued() {
		
		int idOfSelectedButton = _nordOderSuedRadioGroup.getCheckedRadioButtonId();
		
		if (idOfSelectedButton == -1) return HimmelsrichtungEnum.UNBEKANNT;
		
		if (idOfSelectedButton == _nordRadioButton.getId() ) return HimmelsrichtungEnum.NORDEN;
		
		if (idOfSelectedButton == _suedRadioButton.getId() ) return HimmelsrichtungEnum.SUEDEN;
				
		
		Log.e(TAG4LOGGING, "Unerwarteter RadioButton für Nord/Süd gewählt");
		return HimmelsrichtungEnum.UNBEKANNT;		
	}
	
	
	/**
	 * Setzt Zufallskoordinaten, die im zulässigen Bereich liegen;
	 * es wird auch Ost/West bzw. Nord/Süd gesetzt.
	 */
	protected void setzeZufallsKoordinaten() {
		
		Random random = new Random(); // Zufalls-Generator
		
		// Geographische Länge zwischen 0.0 und 180.0 Grad
		double koordinate = random.nextInt(1801) / 10.0;
		_geoLaengeEditText.setText( koordinate + "");

		
		// Geographische Breite zwischen 0.0 und 90.0 Grad
		koordinate = random.nextInt(901) / 10.0;
		_geoBreiteEditText.setText( koordinate + "");
		
		
		// Himmelsrichtungen auswürfeln
		if (random.nextBoolean()) {
			_ostRadioButton.setChecked(true);
		} else {
			_westRadioButton.setChecked(true);
		}
		
		if (random.nextBoolean()) {
			_nordRadioButton.setChecked(true);			
		} else {
			_suedRadioButton.setChecked(true);
		}
	}
	
	
	/**
	 * Methode, mit der "von außen" die mit diesem Fragment
	 * derzeit eingestellte Koordinate abgefragt werden kann.
	 * 
	 * @return "Offizielles" Location-Objekt der Android-API, das z.B. auch für GPS-Ortung verwendet wird;
	 *         gibt {@code null} zurück, wenn derzeit keine zulässige Koordinate
	 *         gewählt ist.
	 */
	public Location getLocation() {

		 if (checkKoordinaten() == false) { return null; }
		 
		 try {
			 
			 String geoLaengeStr = _geoLaengeEditText.getText().toString().trim();
			 double geoLaenge = Double.parseDouble(geoLaengeStr);
			 if (getOstOderWest() == HimmelsrichtungEnum.WESTEN) { // "Westlich" wird durch negatives Vorzeichen dargestellt
				 geoLaenge = -geoLaenge;
			 }
			 
			 String geoBreiteStr = _geoBreiteEditText.getText().toString().trim();
			 double geoBreite = Double.parseDouble(geoBreiteStr);
			 if (getNordOderSued() == HimmelsrichtungEnum.SUEDEN) {
				 geoBreite = -geoBreite;
			 }			 
			 
			 // Eigentliches Erzeugen des Location-Objektes
             Location location = new Location("dummy-provider");
			 location.setLatitude (geoBreite);
			 location.setLongitude(geoLaenge);
			 return location;			 
		 }
		 catch (Exception ex) {
			 Log.e(TAG4LOGGING, "Exception beim Auslesen der Koordianten aufgetreten: " + ex.getMessage());
			 return null;
		 }				 
	}
	
	
	/**
	 * Toast-Objekt mit langer Dauer erscheinen lassen.
	 * 
	 * @param nachricht Text, der mit dem Toast ausgegeben werden soll.
	 */
	protected void zeigeToast(String nachricht) {

		Toast toast = Toast.makeText(getActivity(), nachricht, Toast.LENGTH_LONG);
		toast.show();		
	}
	
};
