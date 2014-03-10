package ro.mmp.tic.activities.streetmap.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.listeners.TypeSelectListener;
import ro.mmp.tic.adapter.model.TypeModel;
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

public class TypeFragment extends Fragment implements TypeSelectListener {

	private static ArrayList<String> seletedType = new ArrayList<String>(0);

	private static ListView listView;
	private ArrayAdapter<TypeModel> dataAdapter;
	private static Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_type_layout,
				container, false);
		context = getActivity().getApplicationContext();
		listView = (ListView) rootView.findViewById(R.id.listView2);

		
		// create an ArrayAdaptar from the String Array
		ArrayAdapter<TypeModel> dataAdapter = new TypeArrayAdapter(context,
				R.layout.fragment_type_layout, getAllTypeModel());

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		return rootView;
	}

	private ArrayList<TypeModel> getAllTypeModel() {
		DataBaseConnection dbc = new DataBaseConnection(context);
		ArrayList<HashMap<String, String>> types = dbc
				.getAllTypes(CategoryFragment.getSelectedCategory());

		ArrayList<TypeModel> allTypes = new ArrayList<TypeModel>(0);

		for (HashMap<String, String> t : types) {
			TypeModel tm = new TypeModel();

			tm.getType().setType(t.get("type"));

			allTypes.add(tm);
		}

		return allTypes;
	}

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */
	private void toastMessage(String text) {

		Toast.makeText(context, text, Toast.LENGTH_LONG).show();

	}

	private class TypeArrayAdapter extends ArrayAdapter<TypeModel> {

		private ArrayList<TypeModel> typeModelList;

		public TypeArrayAdapter(Context context, int textViewResourceId,
				ArrayList<TypeModel> typeModelList) {
			super(context, textViewResourceId, typeModelList);

			this.typeModelList = new ArrayList<TypeModel>();
			this.typeModelList.addAll(typeModelList);
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

				convertView = vi.inflate(R.layout.type_row_layout, null);

				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.type);
				holder.check = (CheckBox) convertView
						.findViewById(R.id.checkType);

				convertView.setTag(holder);

				holder.check.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						TypeModel typeModel = (TypeModel) cb.getTag();

						if (cb.isChecked()) {
							toastMessage((typeModel.getType().getType() + " is selected "));
							seletedType.add(typeModel.getType().getType());
						} else {

							toastMessage(typeModel.getType().getType()
									+ " is unselected ");
							seletedType.remove(typeModel.getType().getType());
						}

						typeModel.setSelected(cb.isChecked());
					}
				});

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			TypeModel state = typeModelList.get(position);

			holder.text.setText(state.getType().getType());
			holder.check.setChecked(state.isSelected());

			for (String s : seletedType) {

				if (s.equals(state.getType().getType())) {
					holder.check.setChecked(true);
				}
			}

			holder.check.setTag(state);

			return convertView;
		}
	}

	@Override
	public void taskFinished() {
		if (!CategoryFragment.getSelectedCategory().isEmpty()) {

			// create an ArrayAdaptar from the String Array
			dataAdapter = new TypeArrayAdapter(context,
					R.layout.fragment_type_layout, getAllTypeModel());

			// Assign adapter to ListView
			listView.setAdapter(dataAdapter);

		}

	}

	public static ArrayList<String> getSeletedType() {
		return seletedType;
	}

	public static void setSeletedType(ArrayList<String> seletedType) {
		TypeFragment.seletedType = seletedType;
	}

}
