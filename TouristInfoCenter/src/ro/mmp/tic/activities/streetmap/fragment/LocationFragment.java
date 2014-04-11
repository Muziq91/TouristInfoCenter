/**
 * @author Matei Mircea
 * 
 * Thos fragment is responsible on selecting the locations based on the  types
 */
package ro.mmp.tic.activities.streetmap.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.listeners.LocationSelectListener;
import ro.mmp.tic.adapter.model.LocationModel;
import ro.mmp.tic.service.sqlite.DataBaseConnection;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationFragment extends Fragment implements
		LocationSelectListener {

	private static ArrayList<String> selectedLocation = new ArrayList<String>(0);
	private static ListView listView;
	private ArrayAdapter<LocationModel> dataAdapter;
	private static Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_location_layout,
				container, false);

		context = getActivity().getApplicationContext();
		listView = (ListView) rootView.findViewById(R.id.listView3);

		// create an ArrayAdaptar from the String Array
		dataAdapter = new LocationArrayAdapter(context,
				R.layout.fragment_location_layout, getAllLocationModel());

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		return rootView;
	}

	private ArrayList<LocationModel> getAllLocationModel() {

		DataBaseConnection dbc = new DataBaseConnection(context);
		ArrayList<HashMap<String, String>> locations = dbc
				.getAllLocations(TypeFragment.getSeletedType());

		ArrayList<LocationModel> allLocations = new ArrayList<LocationModel>(0);

		allLocations.clear();
		for (HashMap<String, String> l : locations) {
			LocationModel lm = new LocationModel();

			lm.getTopic().setName(l.get("name"));

			allLocations.add(lm);
		}

		return allLocations;
	}

	@Override
	public void taskFinished() {
		if (!TypeFragment.getSeletedType().isEmpty()) {

			// create an ArrayAdaptar from the String Array
			dataAdapter = new LocationArrayAdapter(context,
					R.layout.fragment_type_layout, getAllLocationModel());

			// Assign adapter to ListView
			listView.setAdapter(dataAdapter);

		}

	}

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */
	private void toastMessage(String text) {

		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

	}

	private class LocationArrayAdapter extends ArrayAdapter<LocationModel> {

		private ArrayList<LocationModel> locationModelList;

		public LocationArrayAdapter(Context context, int textViewResourceId,
				ArrayList<LocationModel> locationModelList) {
			super(context, textViewResourceId, locationModelList);

			this.locationModelList = new ArrayList<LocationModel>();
			this.locationModelList.addAll(locationModelList);
		}

		private class ViewHolder {
			TextView text;
			CheckBox check;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {

				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = vi.inflate(R.layout.location_row_layout, null);

				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.location);
				holder.check = (CheckBox) convertView
						.findViewById(R.id.checkLocation);

				convertView.setTag(holder);

				holder.check.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						LocationModel locationModel = (LocationModel) cb
								.getTag();

						if (cb.isChecked()) {
							toastMessage((locationModel.getTopic().getName() + " is selected "));
							selectedLocation.add(locationModel.getTopic()
									.getName());
						} else {

							toastMessage(locationModel.getTopic().getName()
									+ " is unselected ");
							selectedLocation.remove(locationModel.getTopic()
									.getName());
						}

						locationModel.setSelected(cb.isChecked());
					}
				});

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			LocationModel state = locationModelList.get(position);

			holder.text.setText(state.getTopic().getName());
			holder.check.setChecked(state.isSelected());

			for (String s : selectedLocation) {

				if (s.equals(state.getTopic().getName())) {
					holder.check.setChecked(true);
				}
			}

			holder.check.setTag(state);

			return convertView;
		}
	}

	public static ArrayList<String> getSelectedLocation() {
		return selectedLocation;
	}

	public static void setSelectedLocation(ArrayList<String> selectedLocation) {
		LocationFragment.selectedLocation = selectedLocation;
	}

}
