package org.duncavage.recyclerviewdemo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;

import org.duncavage.recyclerviewdemo.DemoApplication;
import org.duncavage.recyclerviewdemo.R;
import org.duncavage.recyclerviewdemo.adapters.ListItemViewHolderAdapter;
import org.duncavage.recyclerviewdemo.presenters.DemoDataListPresenter;
import org.duncavage.recyclerviewdemo.presenters.ListPresenter;
import org.duncavage.recyclerviewdemo.presenters.StringProvider;
import org.duncavage.recyclerviewdemo.presenters.views.ListView;
import org.duncavage.recyclerviewdemo.viewmodels.ListItemViewModel;
import org.duncavage.recyclerviewdemo.views.RecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyRecyclerFragment extends Fragment implements ListView<ListItemViewModel> {

    private ListView.Events mEventListener;
    private RecyclerView mRecyclerView;
    private MyListPresenter mListPresenter;

    public enum LayoutType {
        Linear,
        Grid,
        GridWithGroupHeadings,
        GridWithHeadingsAndSpans,
        GridWithCarousel
    }

    public static MyRecyclerFragment newInstance() {
        Bundle args = new Bundle();
        MyRecyclerFragment fragment = new MyRecyclerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private LayoutType layoutType = LayoutType.Linear;
    private ImageLoader mImageLoader = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = DemoApplication.getInstance().getImageLoader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(createLayoutManager(layoutType));
        // Todo ?
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(),
                new RecyclerViewItemClickListener.OnItemGestureListener() {
            @Override
            public void onItemClick(View view, int position) {
                mEventListener.onRemoveItem(position);
                mRecyclerView.getAdapter().notifyItemRemoved(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListPresenter = createListPresenter();
        mListPresenter.load();
    }

    private RecyclerView.LayoutManager createLayoutManager(LayoutType type) {
        if (type == null) {
            return new LinearLayoutManager(getActivity());
        }
        switch (type) {
            case Linear:
                return new LinearLayoutManager(getActivity());
            case Grid:
            case GridWithCarousel:
                return new GridLayoutManager(getActivity(), 2);
            default:
                return new LinearLayoutManager(getActivity());
        }
    }

    private MyListPresenter createListPresenter() {
        StringProvider stringProvider = DemoApplication.getInstance().getStringProvider();
        switch (layoutType) {
            case Linear:
                return new MyListPresenter(this, true, stringProvider);
            case GridWithCarousel:
                break;
        }
        return null;
    }

    @Override
    public void setEventsListener(Events listener) {
        mEventListener = listener;
    }

    @Override
    public void setList(List<ListItemViewModel> list) {
        ListItemViewHolderAdapter adapter;
        if (layoutType == LayoutType.Linear) {
            adapter = new ListItemViewHolderAdapter<>(list, mImageLoader);
            mRecyclerView.swapAdapter(adapter, false);
        }
    }

    public void addNewItem() {
        mEventListener.onAddNewItem();
        mRecyclerView.getAdapter().notifyItemInserted(0);
        mRecyclerView.scrollToPosition(0);
    }
}

class MyListPresenter extends ListPresenter<ListItemViewModel> implements ListView.Events {
    private StringProvider mStringProvider;

    public MyListPresenter(ListView<ListItemViewModel> views,
                           boolean addHeaders,
                           StringProvider stringProvider) {
        super(views);
        views.setEventsListener(this);
        this.mStringProvider = stringProvider;
    }

    @Override
    public void load() {
        List<ListItemViewModel> viewModels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ListItemViewModel model = new ListItemViewModel();
            model.primary = mStringProvider.getStringForResource(R.string.item_primary_prefix) + " " + i;
            model.secondary = mStringProvider.getStringForResource(R.string.item_secondary_prefix) + " " + i;
            model.tertiary = i + "" + mStringProvider.getStringForResource(R.string.item_tertiary_prefix);
            int randAlbum = new Random().nextInt(DemoDataListPresenter.ITEM_IMAGE_URLS.length);
            model.imageUrl = DemoDataListPresenter.ITEM_IMAGE_URLS[randAlbum];
            viewModels.add(model);
        }
        setViewModels(viewModels);
    }

    protected ListItemViewModel createBlankItem() {
        ListItemViewModel model = new ListItemViewModel();
        model.primary = mStringProvider.getStringForResource(R.string.new_item_primary);
        model.secondary = mStringProvider.getStringForResource(R.string.new_item_secondary);
        int randAlbum = new Random().nextInt(DemoDataListPresenter.ITEM_IMAGE_URLS.length);
        model.imageUrl = DemoDataListPresenter.ITEM_IMAGE_URLS[randAlbum];
        return model;
    }

    @Override
    public void onItemClicked(int position) {
        List list = new ArrayList();
        list.add("");
    }

    @Override
    public void onAddNewItem() {
        ListItemViewModel model = createBlankItem();
        getViewModels().add(0, model);
    }

    @Override
    public void onRemoveItem(int position) {
        getViewModels().remove(position);
    }
}
