package huaqin.houlinyuan.a0920_apnsui.ui.fragments;

import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;


public abstract class NotesListFragment extends Fragment {

	@LayoutRes
	protected abstract int getLayoutResId();

	protected abstract int getNumColumns();


//	@Nullable
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		View view = inflater.inflate(getLayoutResId(), container, false);
//
//		// Setup list
////		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.notes_list);
////		recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getNumColumns(),
////				StaggeredGridLayoutManager.VERTICAL));
////		recyclerView.setAdapter(new NotesAdapter(getActivity(), getNumItems()));
//
//		return view;
//	}

}
