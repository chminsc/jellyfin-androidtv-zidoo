package org.jellyfin.androidtv.ui.presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ItemBridgeAdapterShadowOverlayWrapper;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnChildSelectedListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.ShadowOverlayContainer;
import androidx.leanback.widget.ShadowOverlayHelper;

import org.jellyfin.androidtv.databinding.HorizontalGridBinding;

import java.util.Objects;

import timber.log.Timber;

/**
 * A presenter that renders objects in a horizontal grid.
 */
public class HorizontalGridPresenter extends Presenter {
    class HorizontalGridItemBridgeAdapter extends ItemBridgeAdapter {
        @Override
        protected void onCreate(ItemBridgeAdapter.ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ViewGroup) {
                ((ViewGroup) viewHolder.itemView).setTransitionGroup(true);
            }
            if (mShadowOverlayHelper != null) {
                mShadowOverlayHelper.onViewCreated(viewHolder.itemView);
            }
        }

        @Override
        public void onBind(final ItemBridgeAdapter.ViewHolder itemViewHolder) {
            // Only when having an OnItemClickListener, we attach the OnClickListener.
            if (getOnItemViewClickedListener() != null) {
                final View itemView = itemViewHolder.getViewHolder().view;
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getOnItemViewClickedListener() != null) {
                            // Row is always null
                            getOnItemViewClickedListener().onItemClicked(
                                    itemViewHolder.getViewHolder(), itemViewHolder.getItem(), null, null);
                        }
                    }
                });
            }
        }

        @Override
        public void onUnbind(ItemBridgeAdapter.ViewHolder viewHolder) {
            if (getOnItemViewClickedListener() != null) {
                viewHolder.getViewHolder().view.setOnClickListener(null);
            }
        }

        @Override
        public void onAttachedToWindow(ItemBridgeAdapter.ViewHolder viewHolder) {
            viewHolder.itemView.setActivated(true);
        }
    }

    public static class ViewHolder extends Presenter.ViewHolder {
        ItemBridgeAdapter mItemBridgeAdapter;
        final HorizontalGridView mGridView;
        boolean mInitialized;

        public ViewHolder(HorizontalGridView view) {
            super(view);
            mGridView = view;
        }

        public HorizontalGridView getGridView() {
            return mGridView;
        }
    }

    private ViewHolder mViewHolder;
    private int mNumRows = -1;
    private int mFocusZoomFactor;
    private boolean mUseFocusDimmer;
    private boolean mShadowEnabled = true;
    private boolean mKeepChildForeground = true;
    private OnItemViewSelectedListener mOnItemViewSelectedListener;
    private OnItemViewClickedListener mOnItemViewClickedListener;
    private boolean mRoundedCornersEnabled = true;
    ShadowOverlayHelper mShadowOverlayHelper;
    private ItemBridgeAdapter.Wrapper mShadowOverlayWrapper;

    public HorizontalGridPresenter() {
        this(FocusHighlight.ZOOM_FACTOR_LARGE);
    }

    public HorizontalGridPresenter(int focusZoomFactor, boolean useFocusDimmer) {
        mFocusZoomFactor = focusZoomFactor;
        mUseFocusDimmer = useFocusDimmer;
    }

    public HorizontalGridPresenter(int focusZoomFactor) {
        this(focusZoomFactor, true);
    }

    public void setPosition(int ndx) {
        if (mViewHolder != null)
            mViewHolder.getGridView().setSelectedPosition(ndx);
    }

    /**
     * Sets the number of rows in the grid.
     */
    public void setNumberOfRows(int numRows) {
        if (numRows < 0) {
            throw new IllegalArgumentException("Invalid number of rows");
        }
        if (mNumRows != numRows) {
            mNumRows = numRows;
        }
    }

    /**
     * Returns the number of rows in the grid.
     */
    public int getNumberOfRows() {
        return mNumRows;
    }

    /**
     * Enable or disable child shadow.
     * This is not only for enable/disable default shadow implementation but also subclass must
     * respect this flag.
     */
    public final void setShadowEnabled(boolean enabled) {
        mShadowEnabled = enabled;
    }

    /**
     * Returns true if child shadow is enabled.
     * This is not only for enable/disable default shadow implementation but also subclass must
     * respect this flag.
     */
    public final boolean getShadowEnabled() {
        return mShadowEnabled;
    }

    /**
     * Returns true if opticalBounds is supported (SDK >= 18) so that default shadow
     * is applied to each individual child of {@link HorizontalGridView}.
     * Subclass may return false to disable.
     */
    public boolean isUsingDefaultShadow() {
        return ShadowOverlayContainer.supportsShadow();
    }

    /**
     * Enables or disabled rounded corners on children of this row.
     * Supported on Android SDK >= L.
     */
    public final void enableChildRoundedCorners(boolean enable) {
        mRoundedCornersEnabled = enable;
    }

    /**
     * Returns true if rounded corners are enabled for children of this row.
     */
    public final boolean areChildRoundedCornersEnabled() {
        return mRoundedCornersEnabled;
    }

    /**
     * Returns true if SDK >= L, where Z shadow is enabled so that Z order is enabled
     * on each child of vertical grid.   If subclass returns false in isUsingDefaultShadow()
     * and does not use Z-shadow on SDK >= L, it should override isUsingZOrder() return false.
     */
    public boolean isUsingZOrder() {
        return false;
    }

    final boolean needsDefaultShadow() {
        return isUsingDefaultShadow() && getShadowEnabled();
    }

    /**
     * Returns the zoom factor used for focus highlighting.
     */
    public final int getFocusZoomFactor() {
        return mFocusZoomFactor;
    }

    /**
     * Returns true if the focus dimmer is used for focus highlighting; false otherwise.
     */
    public final boolean isFocusDimmerUsed() {
        return mUseFocusDimmer;
    }

    protected ShadowOverlayHelper.Options createShadowOverlayOptions() {
        return ShadowOverlayHelper.Options.DEFAULT;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent) {
        mViewHolder = createGridViewHolder(parent);
        mViewHolder.mInitialized = false;
        mViewHolder.mItemBridgeAdapter = new HorizontalGridItemBridgeAdapter();
        initializeGridViewHolder(mViewHolder);
        if (!mViewHolder.mInitialized) {
            throw new RuntimeException("super.initializeGridViewHolder() must be called");
        }
        return mViewHolder;
    }

    /**
     * Subclass may override this to inflate a different layout.
     */
    protected ViewHolder createGridViewHolder(ViewGroup parent) {
        HorizontalGridBinding binding = HorizontalGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.horizontalGrid);
    }

    /**
     * Called after a {@link HorizontalGridPresenter.ViewHolder} is created.
     * Subclasses may override this method and start by calling
     * super.initializeGridViewHolder(ViewHolder).
     *
     * @param vh The ViewHolder to initialize for the vertical grid.
     */
    protected void initializeGridViewHolder(ViewHolder vh) {
        if (mNumRows == -1) {
            throw new IllegalStateException("Number of rows must be set");
        }
        Timber.d("mNumRows %s", mNumRows);
        vh.getGridView().setNumRows(mNumRows);
        vh.mInitialized = true;

        Context context = vh.mGridView.getContext();
        if (mShadowOverlayHelper == null) {
            mShadowOverlayHelper = new ShadowOverlayHelper.Builder()
                    .needsOverlay(mUseFocusDimmer)
                    .needsShadow(needsDefaultShadow())
                    .needsRoundedCorner(areChildRoundedCornersEnabled())
                    .preferZOrder(isUsingZOrder())
                    .keepForegroundDrawable(mKeepChildForeground)
                    .options(createShadowOverlayOptions())
                    .build(context);
            if (mShadowOverlayHelper.needsWrapper()) {
                mShadowOverlayWrapper = new ItemBridgeAdapterShadowOverlayWrapper(
                        mShadowOverlayHelper);
            }
        }

        vh.mItemBridgeAdapter.setWrapper(mShadowOverlayWrapper);
        mShadowOverlayHelper.prepareParentForShadow(vh.mGridView);
        vh.getGridView().setFocusDrawingOrderEnabled(mShadowOverlayHelper.getShadowType()
                != ShadowOverlayHelper.SHADOW_DYNAMIC);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(vh.mItemBridgeAdapter,
                mFocusZoomFactor, mUseFocusDimmer);

        final ViewHolder gridViewHolder = vh;
        vh.getGridView().setOnChildSelectedListener(new OnChildSelectedListener() {
            @Override
            public void onChildSelected(ViewGroup parent, View view, int position, long id) {
                selectChildView(gridViewHolder, view);
            }
        });
    }

    /**
     * Set if keeps foreground of child of this grid, the foreground will not
     * be used for overlay color.  Default value is true.
     *
     * @param keep   True if keep foreground of child of this grid.
     */
    public final void setKeepChildForeground(boolean keep) {
        mKeepChildForeground = keep;
    }

    /**
     * Returns true if keeps foreground of child of this grid, the foreground will not
     * be used for overlay color.  Default value is true.
     *
     * @return   True if keeps foreground of child of this grid.
     */
    public final boolean getKeepChildForeground() {
        return mKeepChildForeground;
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Timber.d("onBindViewHolder %s", item);
        Objects.requireNonNull(item);
        ViewHolder mViewHolder = (ViewHolder) viewHolder;
        mViewHolder.mItemBridgeAdapter.setAdapter((ObjectAdapter) item);
        mViewHolder.getGridView().setAdapter(mViewHolder.mItemBridgeAdapter);
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        Timber.d("onUnbindViewHolder");
        ViewHolder vh = (ViewHolder) viewHolder;
        vh.mItemBridgeAdapter.setAdapter(null);
        vh.getGridView().setAdapter(null);
    }

    /**
     * Sets the item selected listener.
     * Since this is a grid the row parameter is always null.
     */
    public final void setOnItemViewSelectedListener(OnItemViewSelectedListener listener) {
        mOnItemViewSelectedListener = listener;
    }

    /**
     * Returns the item selected listener.
     */
    public final OnItemViewSelectedListener getOnItemViewSelectedListener() {
        return mOnItemViewSelectedListener;
    }

    /**
     * Sets the item clicked listener.
     * OnItemViewClickedListener will override {@link View.OnClickListener} that
     * item presenter sets during {@link Presenter#onCreateViewHolder(ViewGroup)}.
     * So in general, developer should choose one of the listeners but not both.
     */
    public final void setOnItemViewClickedListener(OnItemViewClickedListener listener) {
        mOnItemViewClickedListener = listener;
    }

    /**
     * Returns the item clicked listener.
     */
    public final OnItemViewClickedListener getOnItemViewClickedListener() {
        return mOnItemViewClickedListener;
    }

    private void selectChildView(ViewHolder vh, View view) {
        if (getOnItemViewSelectedListener() != null) {
            ItemBridgeAdapter.ViewHolder ibh = (view == null) ? null :
                    (ItemBridgeAdapter.ViewHolder) vh.getGridView().getChildViewHolder(view);
            if (ibh == null) {
                getOnItemViewSelectedListener().onItemSelected(null, null, null, null);
            } else {
                getOnItemViewSelectedListener().onItemSelected(ibh.getViewHolder(), ibh.getItem(), null, null);
            }
        }
    }

    /**
     * Changes the visibility of views.  The entrance transition will be run against the views that
     * change visibilities.  This method is called by the fragment, it should not be called
     * directly by the application.
     *
     * @param holder         The ViewHolder for the vertical grid.
     * @param afterEntrance  true if children of vertical grid participating in entrance transition
     *                       should be set to visible, false otherwise.
     */
    public void setEntranceTransitionState(HorizontalGridPresenter.ViewHolder holder,
                                           boolean afterEntrance) {
        holder.mGridView.setChildrenVisibility(afterEntrance? View.VISIBLE : View.INVISIBLE);
    }
}

