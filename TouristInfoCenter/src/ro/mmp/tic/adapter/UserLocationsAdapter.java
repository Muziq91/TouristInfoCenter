package ro.mmp.tic.adapter;

import java.util.ArrayList;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.custommap.usertopics.ManageUserTopicsActivity;
import ro.mmp.tic.activities.streetmap.util.ImageUtil;
import ro.mmp.tic.adapter.model.UserLocationModel;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class UserLocationsAdapter extends ArrayAdapter<UserLocationModel> {

	private ArrayList<UserLocationModel> data;
	private Context context;
	private ImageUtil imageUtil;

	public UserLocationsAdapter(Context context, int textViewResourceId,
			ArrayList<UserLocationModel> data) {
		super(context, textViewResourceId, data);
		this.context = context;
		this.data = new ArrayList<UserLocationModel>();
		this.data.addAll(data);
		imageUtil = new ImageUtil(context);
	}

	private class ViewHolder {
		TextView userLocationName;
		TextView userLocationDescription;
		TextView userLocationLat;
		TextView userLocationLng;
		ImageView userLocationImage;

		CheckBox userLocationCheck;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;


		if (convertView == null) {

			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = vi.inflate(R.layout.user_locations_row_layout, null);

			holder = new ViewHolder();

			holder.userLocationCheck = (CheckBox) convertView
					.findViewById(R.id.userLocationCheck);
			holder.userLocationName = (TextView) convertView
					.findViewById(R.id.userLocationName);
			holder.userLocationDescription = (TextView) convertView
					.findViewById(R.id.userLocationDescription);
			holder.userLocationLat = (TextView) convertView
					.findViewById(R.id.userLocationLat);
			holder.userLocationLng = (TextView) convertView
					.findViewById(R.id.userLocationLng);
			holder.userLocationImage = (ImageView) convertView
					.findViewById(R.id.userLocationImage);

			convertView.setTag(holder);

			holder.userLocationCheck
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							CheckBox cb = (CheckBox) v;
							UserLocationModel fieldValue = (UserLocationModel) cb
									.getTag();

							if (cb.isChecked()) {

								ManageUserTopicsActivity
										.getUserCustomLocation().add(
												fieldValue.getUserLocation()
														.getName());

							} else {

								ManageUserTopicsActivity
										.getUserCustomLocation().remove(
												fieldValue.getUserLocation()
														.getName());
							}

							fieldValue.setChecked(cb.isChecked());
						}
					});

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		UserLocationModel state = data.get(position);

		holder.userLocationName.setText(state.getUserLocation().getName());
		holder.userLocationDescription.setText(state.getUserLocation()
				.getDescription());
		holder.userLocationLat.setText("" + state.getUserLocation().getLat());
		holder.userLocationLng.setText("" + state.getUserLocation().getLng());

		holder.userLocationImage.setImageBitmap(imageUtil.getThumbnail(state
				.getUserLocation().getImage()));
		holder.userLocationCheck.setChecked(state.isChecked());

		holder.userLocationCheck.setTag(state);

		return convertView;
	}
}
