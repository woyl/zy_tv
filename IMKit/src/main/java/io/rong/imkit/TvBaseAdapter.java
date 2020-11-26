package io.rong.imkit;


import android.database.DataSetObservable;
import android.view.View;
import android.view.ViewGroup;

public abstract class TvBaseAdapter {

	private DataSetObservable observable;

	public abstract int getCount();

	public abstract Object getItem(int position);

	public abstract long getItemId(int position);

	public abstract View getView(int position, View contentView,
			ViewGroup parent);

	/**
	 * ͨ��DATAOBSERVERˢ����������ͼ
	 */
	public void notifyDataSetChanged() {
		if (observable != null) {
			observable.notifyChanged();
		}
	}

	public void registerDataSetObservable(DataSetObservable observer) {

		observable = observer;
	}

	public void unregisterDataSetObservable(DataSetObservable observer) {
		this.observable = null;
	}

}
