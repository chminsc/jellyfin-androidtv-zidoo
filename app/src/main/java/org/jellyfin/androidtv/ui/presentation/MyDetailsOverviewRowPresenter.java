package org.jellyfin.androidtv.ui.presentation;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.RowPresenter;

import org.jellyfin.androidtv.data.model.InfoItem;
import org.jellyfin.androidtv.databinding.ViewRowDetailsBinding;
import org.jellyfin.androidtv.ui.DetailRowView;
import org.jellyfin.androidtv.ui.TextUnderButton;
import org.jellyfin.androidtv.ui.itemdetail.MyDetailsOverviewRow;
import org.jellyfin.androidtv.util.MarkdownRenderer;
import org.jellyfin.sdk.model.api.BaseItemDto;
import org.jellyfin.sdk.model.api.BaseItemKind;

public class MyDetailsOverviewRowPresenter extends RowPresenter {
    private final MarkdownRenderer markdownRenderer;
    private ViewHolder viewHolder;

    @Override
    public boolean isUsingDefaultSelectEffect() {
        return false;
    }

    public MyDetailsOverviewRowPresenter(MarkdownRenderer markdownRenderer) {
        super();

        this.markdownRenderer = markdownRenderer;

        // Don't call setActivated() on views
        setSyncActivatePolicy(SYNC_ACTIVATED_CUSTOM);
        setHeaderPresenter(null);
    }

    public final class ViewHolder extends RowPresenter.ViewHolder {
        ViewRowDetailsBinding binding;

        public ViewHolder(DetailRowView view) {
            super(view);
            binding = view.getBinding();
        }
    }

    @Override
    protected ViewHolder createRowViewHolder(ViewGroup parent) {
        DetailRowView view = new DetailRowView(parent.getContext());
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);

        MyDetailsOverviewRow row = (MyDetailsOverviewRow) item;
        var vh = (ViewHolder) holder;
        if (vh == null || vh.binding == null)
            return;

        setTitle(row.getItem().getName());
        vh.binding.fdMainInfoRow.setBaseItem(row.getItem());
        if (row.MediaSourceIndex > 0)
            vh.binding.fdMainInfoRow.setMediaSourceIndex(row.MediaSourceIndex);
        addGenres(vh.binding.fdGenreRow, row.getItem());
        setInfo1(row.getInfoItem1());
        setInfo2(row.getInfoItem2());
        setInfo3(row.getInfoItem3());

        String posterUrl = row.getImageDrawable();
        vh.binding.mainImage.load(posterUrl, null, null, 1.0, 0);

        String summaryRaw = row.getSummary();
        if (summaryRaw != null)
            vh.binding.fdSummaryText.setText(markdownRenderer.toMarkdownSpanned(summaryRaw));

        if (row.getItem().getType() == BaseItemKind.PERSON) {
            vh.binding.fdGenreRow.setVisibility(View.GONE);
            vh.binding.fdMainInfoRow.setVisibility(View.VISIBLE);
        }

        vh.binding.fdButtonRow.removeAllViews();
        for (TextUnderButton button : row.getActions()) {
            vh.binding.fdButtonRow.addView(button);
        }
    }

    private void addGenres(TextView textView, BaseItemDto item) {
        textView.setText(TextUtils.join(" / ", item.getGenres()));
    }

    public void setTitle(String text) {
        viewHolder.binding.fdTitle.setText(text);
    }

    public void setInfo1(InfoItem info) {
        if (info == null) {
            viewHolder.binding.infoTitle1.setText("");
            viewHolder.binding.infoValue1.setText("");
        } else {
            viewHolder.binding.infoTitle1.setText(info.getLabel());
            viewHolder.binding.infoValue1.setText(info.getValue());
        }
    }

    public void setInfo2(InfoItem info) {
        if (info == null) {
            viewHolder.binding.infoTitle2.setText("");
            viewHolder.binding.infoValue2.setText("");
        } else {
            viewHolder.binding.infoTitle2.setText(info.getLabel());
            viewHolder.binding.infoValue2.setText(info.getValue());
        }
    }

    public void setInfo3(InfoItem info) {
        if (info == null) {
            viewHolder.binding.infoTitle3.setText("");
            viewHolder.binding.infoValue3.setText("");
        } else {
            viewHolder.binding.infoTitle3.setText(info.getLabel());
            viewHolder.binding.infoValue3.setText(info.getValue());
        }
    }

    public TextView getSummaryView() {
        return viewHolder.binding.fdSummaryText;
    }

    public void updateEndTime(String text) {
        if (viewHolder != null && viewHolder.binding.infoTitle3.length() > 0)
            viewHolder.binding.infoValue3.setText(text);
    }

    @Override
    protected void onSelectLevelChanged(RowPresenter.ViewHolder holder) {
        // Do nothing - this removes the shadow on the out of focus rows of image cards
    }
}
