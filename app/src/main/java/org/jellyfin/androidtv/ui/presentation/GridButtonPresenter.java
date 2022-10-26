package org.jellyfin.androidtv.ui.presentation;

import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.Presenter;

import org.jellyfin.androidtv.constant.CardInfoType;
import org.jellyfin.androidtv.ui.GridButton;
import org.jellyfin.androidtv.ui.card.LegacyImageCardView;
import org.jellyfin.androidtv.util.ImageUtils;

public class GridButtonPresenter extends Presenter {
    private boolean mShowInfo;

    public GridButtonPresenter() {
        this(true);
    }

    public GridButtonPresenter(boolean showInfo) {
        super();
        mShowInfo = showInfo;
    }

    class ViewHolder extends Presenter.ViewHolder {
        private GridButton gridButton;
        private final LegacyImageCardView cardView;

        public ViewHolder(View view) {
            super(view);
            cardView = (LegacyImageCardView) view;
        }

        public void setItem(GridButton button) {
            gridButton = button;
            cardView.getMainImageView().setImageResource(gridButton.getImageRes());
            cardView.setTitleText(gridButton.getText());
        }

        public GridButton getItem() {
            return gridButton;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        LegacyImageCardView cardView = new LegacyImageCardView(parent.getContext(), mShowInfo ? CardInfoType.UNDER_MINI : CardInfoType.NO_INFO, parent);
        cardView.setMainImageAspect(ImageUtils.ASPECT_RATIO_SQUARE);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        if (!(item instanceof GridButton)) return;
        GridButton gridItem = (GridButton) item;

        ViewHolder vh = (ViewHolder) viewHolder;
        vh.setItem(gridItem);
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
    }
}
