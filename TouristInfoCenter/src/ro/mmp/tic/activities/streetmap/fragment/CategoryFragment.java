/**
 * @author Matei Mircea
 * 
 * This fragment is responsible for displaying all the categories.
 * The user should select a  category in order to select a type and after selecting the type, the
 * user can select a lcoation
 */
package ro.mmp.tic.activities.streetmap.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.adapter.model.CategoryModel;
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

public class CategoryFragment extends Fragment {

	private static ArrayList<String> selectedCategory = new ArrayList<String>(0);

	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		View rootView = inflater.inflate(R.layout.fragment_category_layout,
				container, false);

		// create an ArrayAdaptar from the String Array
		ArrayAdapter<CategoryModel> dataAdapter = new CategoryArrayAdapter(
				getActivity().getApplicationContext(),
				R.layout.fragment_category_layout, getAllCategoryModel());

		/*
		 * listView = (ListView) getActivity().findViewById( R.id.listView1);
		 */

		listView = (ListView) rootView.findViewById(R.id.listView1);

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		return rootView;
	}

	private ArrayList<CategoryModel> getAllCategoryModel() {

		DataBaseConnection dbc = new DataBaseConnection(getActivity()
				.getApplicationContext());
		ArrayList<HashMap<String, String>> categories = dbc.getAllCategory();
		ArrayList<CategoryModel> allCategories = new ArrayList<CategoryModel>(0);

		for (HashMap<String, String> c : categories) {
			CategoryModel cm = new CategoryModel();

			cm.getCategory().setCategory(c.get("category"));

			allCategories.add(cm);
		}

		return allCategories;
	}

	/**
	 * Creates andSends a Toast message
	 * 
	 * @param text
	 */
	private void toastMessage(String text) {

		Toast.makeText(getActivity().getApplicationContext(), text,
				Toast.LENGTH_SHORT).show();

	}

	public static ArrayList<String> getSelectedCategory() {
		return selectedCategory;
	}

	public static void setSelectedCategory(ArrayList<String> selectedCategory) {
		CategoryFragment.selectedCategory = selectedCategory;
	}

	private class CategoryArrayAdapter extends ArrayAdapter<CategoryModel> {

		private ArrayList<CategoryModel> categoryModelList;

		public CategoryArrayAdapter(Context context, int textViewResourceId,
				ArrayList<CategoryModel> categoryModelList) {
			super(context, textViewResourceId, categoryModelList);
			this.categoryModelList = new ArrayList<CategoryModel>();
			this.categoryModelList.addAll(categoryModelList);
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

				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = vi.inflate(R.layout.category_row_layout, null);

				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.category);
				holder.check = (CheckBox) convertView
						.findViewById(R.id.checkCategory);

				convertView.setTag(holder);

				holder.check.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						CategoryModel categoryModel = (CategoryModel) cb
								.getTag();

						if (cb.isChecked()) {
							toastMessage((categoryModel.getCategory()
									.getCategory() + " is selected "));
							selectedCategory.add(categoryModel.getCategory()
									.getCategory());
						} else {

							toastMessage(categoryModel.getCategory()
									.getCategory() + " is unselected ");
							selectedCategory.remove(categoryModel.getCategory()
									.getCategory());
						}

						categoryModel.setSelected(cb.isChecked());
					}
				});

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			CategoryModel state = categoryModelList.get(position);

			holder.text.setText(state.getCategory().getCategory());
			holder.check.setChecked(state.isSelected());

			for (String s : selectedCategory) {

				if (s.equals(state.getCategory().getCategory())) {
					holder.check.setChecked(true);
				}
			}

			holder.check.setTag(state);

			return convertView;
		}
	}

}
