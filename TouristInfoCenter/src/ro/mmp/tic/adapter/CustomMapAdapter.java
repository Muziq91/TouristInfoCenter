package ro.mmp.tic.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import ro.mmp.tic.R;
import ro.mmp.tic.activities.streetmap.util.ImageUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomMapAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	private ImageUtil imageUtil;

	public CustomMapAdapter(Context context,
			ArrayList<HashMap<String, String>> data) {
		this.context = context;
		this.data = data;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		imageUtil = new ImageUtil(context);
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.custom_map_row_layout, null);
		}

		TextView customMapNameLocation = (TextView) vi
				.findViewById(R.id.customMapNameLocation);
		TextView customMapNameDescription = (TextView) vi
				.findViewById(R.id.customMapNameDescription);
		TextView customMapNameLat = (TextView) vi
				.findViewById(R.id.customMapNameLat);
		TextView customMapNameLng = (TextView) vi
				.findViewById(R.id.customMapNameLng);
		TextView customMapNamecolor = (TextView) vi
				.findViewById(R.id.customMapNamecolor);
		ImageView customMapImage = (ImageView) vi
				.findViewById(R.id.customMapImage);

		HashMap<String, String> location = new HashMap<String, String>(0);
		location = data.get(position);

		customMapNameLocation.setText("Name: " + location.get("NAME"));
		customMapNameDescription.setText("Description: "
				+ location.get("DESCRIPTION"));
		customMapNameLat.setText("lat: " + location.get("LAT"));
		customMapNameLng.setText("lng: " + location.get("LNG"));
		customMapNamecolor.setText("color: " + location.get("COLOR"));
		customMapImage.setImageBitmap(imageUtil.getThumbnail(location
				.get("IMAGE")));

		return vi;

	}
}