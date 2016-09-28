package huaqin.houlinyuan.a0920_apnsui.ui.adapters;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import huaqin.houlinyuan.a0920_apnsui.R;
import huaqin.houlinyuan.a0920_apnsui.apn_crud.ApnDatilActivity;
import huaqin.houlinyuan.a0920_apnsui.database.Apns;
import huaqin.houlinyuan.a0920_apnsui.helper.ItemTouchHelperAdapter;
import huaqin.houlinyuan.a0920_apnsui.helper.ItemTouchHelperViewHolder;
import huaqin.houlinyuan.a0920_apnsui.helper.OnStartDragListener;
import huaqin.houlinyuan.a0920_apnsui.ui.models.Note;

/**
 * Created by Gordon Wong on 7/18/2015.
 *
 * Adapter for the all items screen.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> implements ItemTouchHelperAdapter {
	private static final String TAG = "hlyNotesAdapter";
	private static final String TAGA = "hlyDDAANotesAdapter";
	private Note[] notes;
	private Context mContext;
	private ContentResolver resolver;
	private ArrayList<Map<String,String>> apnlist;
	private String whereCalues;
	private Resources resources;

	private ArrayList<HashMap<String,String>> mlistitem;

	private  OnStartDragListener mDragStartListener;
	public NotesAdapter(Context context,String selection, OnStartDragListener dragStartListener) {
		Log.d(TAGA , "-----NotesAdapter------selection=" + selection);
		mDragStartListener = dragStartListener;
		mContext = context;
		resolver = mContext.getContentResolver();
		resources = mContext.getResources();
		whereCalues = selection;
		mlistitem = new ArrayList<HashMap<String,String>>();
		mlistitem = getCursor(whereCalues);
		notes = generateNotes(context, getItemCount());

	}

	@Override
	public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_note, parent,
				false);
		Log.d(TAG , "-----onCreateViewHolder------");
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		Log.d(TAG , "-----onBindViewHolder------ ");
		Log.d(TAG , "-----onBindViewHolder------position=" + position);
		HashMap<String,String> item = new HashMap<String,String>();
		item = mlistitem.get(position);


		String _id = item.get("_id");
		String carrier = item.get("carrier");
		String mcc = item.get("mcc");
		String mnc = item.get("mnc");

		Note noteModel = notes[position];
		int color = noteModel.getColor();
		Log.d(TAG , "-----onBindViewHolder------position=" + position + "--id=" + _id + "--carrier= " + carrier);
		// Set text
		holder.holder_textView_carrier.setText(carrier);
		holder.holder_textView_mcc.setText(mcc);
		holder.holder_textView_mnc.setText(mnc);
		holder.holder_textView_id.setText(_id);

		// Set visibilities
		holder.holder_textView_carrier.setVisibility(TextUtils.isEmpty(carrier) ? View.GONE : View.VISIBLE);
		holder.holder_textView_mcc.setVisibility(TextUtils.isEmpty(mcc) ? View.GONE : View.VISIBLE);
		holder.holder_textView_mnc.setVisibility(TextUtils.isEmpty(mnc) ? View.GONE : View.VISIBLE);

		// Set padding
		int paddingTop = (holder.holder_textView_carrier.getVisibility() != View.VISIBLE) ? 0
				: holder.itemView.getContext().getResources()
				.getDimensionPixelSize(R.dimen.note_content_spacing);
		holder.holder_textView_mcc.setPadding(holder.holder_textView_mcc.getPaddingLeft(), paddingTop,
				holder.holder_textView_mcc.getPaddingRight(), holder.holder_textView_mcc.getPaddingBottom());

		// Set background color
		holder.itemView.setMinimumHeight(120);

		holder.holder_mainlayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent intent = new Intent(mContext,ApnDatilActivity.class);
				intent.putExtra("number", Integer.valueOf(holder.holder_textView_id.getText().toString().trim()));
				mContext.startActivity(intent);
				Log.d(TAG , "-----holder_mainlayout------ id=" + holder.holder_textView_id.getText().toString().trim());
			}
		});


		((CardView) holder.itemView).setCardBackgroundColor(color);
		Log.d(TAG , "-----onBindViewHolder------ end");
	}

	@Override
	public int getItemCount() {
		Log.d(TAG , "-----getItemCount------getItemCount="  + mlistitem.size());
		return mlistitem.size();
	}

	private Note[] generateNotes(Context context, int numNotes) {
		Note[] notes = new Note[numNotes];
		for (int i = 0; i < notes.length; i++) {
			notes[i] = Note.randomNote(context);
		}
		return notes;
	}

	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		Collections.swap(mlistitem, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);
		Log.d(TAGA , "-----onItemMove------fromPosition="  + fromPosition+"----toPosition="+toPosition);
		return true;
	}

	@Override
	public void onItemDismiss(int position) {
		Uri uri = ContentUris.withAppendedId(Apns.Apn.APN_CONTENT_URI,Integer.valueOf(mlistitem.get(position).get("_id").trim()));
		resolver.delete(uri,null,null);
		mlistitem.remove(position);
		notifyItemRemoved(position);
		Log.d(TAGA , "-----onItemDismiss------position="  + position );
	}


	public  class ViewHolder extends RecyclerView.ViewHolder implements
			ItemTouchHelperViewHolder
	{

		public TextView holder_textView_carrier;
		public TextView holder_textView_mcc;
		public TextView holder_textView_mnc;
		public TextView holder_textView_id;
		public LinearLayout holder_infoLayout;
		public RelativeLayout holder_mainlayout;


		//自定义ViewHolder，用于Listview getview优化
		public ViewHolder(View itemView) {
			super(itemView);
			holder_textView_carrier = (TextView) itemView.findViewById(R.id.listitem_carrier);
			holder_textView_mcc = (TextView) itemView.findViewById(R.id.listitem_mcc);
			holder_textView_mnc = (TextView) itemView.findViewById(R.id.listitem_mnc);
			holder_textView_id = (TextView) itemView.findViewById(R.id.listitem_id);
			holder_infoLayout = (LinearLayout) itemView.findViewById(R.id.note_info_layout);
			holder_mainlayout = (RelativeLayout)itemView.findViewById(R.id.listitem_mainlayout);
			}

				 @Override
				 public void onItemSelected(){

				 }

				 @Override
				 public void onItemClear() {

				 }
	}
	public ArrayList<HashMap<String,String>> getCursor(String selection)
	{
		ArrayList<HashMap<String,String>> listitem = new ArrayList<HashMap<String,String>>();
		Log.d(TAGA, "----------initListView--------selection=" + selection);
		if (selection != null)
		{
			Log.d(TAG, "----------selection--------selection=" + selection);
		}
		Cursor cursor = resolver.query(Apns.Apn.DICT_CONTENT_URI, null, selection, null, null);
		String _id = "";
		String carrier = "";
		String mcc = "";
		String mnc = "";
		while (cursor.moveToNext())
		{
			HashMap<String,String> item = new HashMap<String,String>();
			_id = cursor.getString(cursor.getColumnIndex("_id"));
			carrier = cursor.getString(cursor.getColumnIndex("carrier"));
			mcc = cursor.getString(cursor.getColumnIndex("mcc"));
			mnc = cursor.getString(cursor.getColumnIndex("mnc"));
			item.put("_id",_id);
			item.put("carrier",carrier);
			item.put("mcc",mcc);
			item.put("mnc",mnc);
			listitem.add(item);
		}
		cursor.close();
		return listitem;
	}

}
