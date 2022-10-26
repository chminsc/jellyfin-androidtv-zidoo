package org.jellyfin.androidtv.ui.presentation;

import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.RowPresenter;

import org.jellyfin.androidtv.preference.UserPreferences;
import org.jellyfin.androidtv.util.LayoutHelper;
import org.jellyfin.androidtv.util.Utils;
import org.koin.java.KoinJavaComponent;

public class CustomListRowPresenter extends ListRowPresenter {
    protected ListRowPresenter.ViewHolder viewHolder;
    public final Integer rowHeightPx;
    public final Integer headerHeightPx;

    public CustomListRowPresenter() {
        super(KoinJavaComponent.<UserPreferences>get(UserPreferences.class).get(UserPreferences.Companion.getFocusZoomSize()).getValue(),false); // dimmer = per card/item dimm-effect
        var containerHeights = LayoutHelper.INSTANCE.getDefaultRowHeights();
        this.rowHeightPx = containerHeights.getFirst();
        this.headerHeightPx = containerHeights.getSecond();
        init();
    }

    public CustomListRowPresenter(int rowHeightPx, int headerHeightPx) {
        super(KoinJavaComponent.<UserPreferences>get(UserPreferences.class).get(UserPreferences.Companion.getFocusZoomSize()).getValue(),false); // dimmer = per card/item dimm-effect
        this.rowHeightPx = rowHeightPx;
        this.headerHeightPx = headerHeightPx;
        init();
    }

    private void init() {
        setSelectEffectEnabled(KoinJavaComponent.<UserPreferences>get(UserPreferences.class).get(UserPreferences.Companion.getEnableFocusDimming())); // row select dimming
        setKeepChildForeground(false); // use dimming without extra helper overlay frames
        enableChildRoundedCorners(false);
        setShadowEnabled(false);

        setHeaderPresenter(new CustomRowHeaderPresenter(headerHeightPx, 0));
        getHeaderPresenter().setNullItemVisibilityGone(true);

        setRowHeight(rowHeightPx);
    }

    public int getRowContainerHeight() {
        return rowHeightPx + headerHeightPx;
    }
    @Override
    public boolean isUsingDefaultShadow() {
        return false;
    }
    @Override
    public boolean isUsingDefaultListSelectEffect() {
        return true;
    }

//    @Override
//    public boolean isUsingOutlineClipping(Context context) {
//        return true;
//    }

    @Override
    protected boolean isClippingChildren() {
        return false;
    }

    @Override
    protected void initializeRowViewHolder(RowPresenter.ViewHolder holder) {
        viewHolder = (ViewHolder) holder;

        if (viewHolder != null && viewHolder.getGridView() != null) {
            var gridView = viewHolder.getGridView();
            var cardSpacing = KoinJavaComponent.<UserPreferences>get(UserPreferences.class).get(UserPreferences.Companion.getCardSpacing());
            var spacingPX = Utils.convertDpToPixel(
                    viewHolder.view.getContext(),
                    cardSpacing.getSizeDP()
            );
            gridView.setHorizontalSpacing(spacingPX);
        }

        super.initializeRowViewHolder(holder);
    }

//    @Override
//    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
//        // Hide header view when the item doesn't have one
//        if (holder.getHeaderViewHolder() != null) {
//            if (item instanceof ListRow) {
//                var headerItem = ((ListRow) item).getHeaderItem();
//                ViewKt.setVisible(holder.getHeaderViewHolder().view, headerItem != null);
//            }
//        }
//        super.onBindRowViewHolder(holder, item);
//    }
}
