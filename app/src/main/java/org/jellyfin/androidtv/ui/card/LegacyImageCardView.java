package org.jellyfin.androidtv.ui.card;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
import static org.jellyfin.androidtv.util.ImageUtils.ASPECT_RATIO_BANNER;
import static org.jellyfin.androidtv.util.ImageUtils.ASPECT_RATIO_POSTER;
import static org.koin.java.KoinJavaComponent.inject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.VerticalGridView;

import org.jellyfin.androidtv.R;
import org.jellyfin.androidtv.constant.CardInfoType;
import org.jellyfin.androidtv.constant.ColorSelectionBG;
import org.jellyfin.androidtv.constant.FocusBorderSize;
import org.jellyfin.androidtv.constant.FocusIconSize;
import org.jellyfin.androidtv.databinding.ViewCardLegacyImageBinding;
import org.jellyfin.androidtv.preference.UserPreferences;
import org.jellyfin.androidtv.preference.constant.RatingType;
import org.jellyfin.androidtv.ui.AsyncImageView;
import org.jellyfin.androidtv.ui.browsing.GenericGridActivity;
import org.jellyfin.androidtv.ui.itemhandling.BaseRowItem;
import org.jellyfin.androidtv.util.ContextExtensionsKt;
import org.jellyfin.androidtv.util.DrawableBuilder;
import org.jellyfin.androidtv.util.TextViewExtensionKt;
import org.jellyfin.androidtv.util.Utils;
import org.jellyfin.androidtv.util.ViewLayoutExtensionKt;
import org.jellyfin.apiclient.model.dto.BaseItemDto;
import org.jellyfin.apiclient.model.dto.BaseItemType;

import java.text.NumberFormat;
import java.util.Locale;

import kotlin.Lazy;
import timber.log.Timber;

/**
 * Modified CardView with no fade on the badge
 * A card view with an {@link AsyncImageView} as its main region.
 */
public class LegacyImageCardView extends CardView {
    public int MAX_DIAGONAL;
    public int MIN_DIAGONAL;
    private ViewCardLegacyImageBinding binding;
    private AsyncImageView imageView;
    private final NumberFormat nf = NumberFormat.getInstance();
    private int mWidth = -1;
    private int mHeight = -1;
    private int mTextSizeSP;
    private boolean isGridActivity = false;

    private final Lazy<UserPreferences> userPreferences = inject(UserPreferences.class);

    public LegacyImageCardView(Context context) {
        super(context);
        init(context, CardInfoType.NO_INFO, null);
    }

    public LegacyImageCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, CardInfoType.NO_INFO, null);
    }

    public LegacyImageCardView(Context context, CardInfoType infoType, ViewGroup parent) {
        super(context);
        init(context, infoType, parent);
    }

    private void init(Context context, CardInfoType infoType, ViewGroup parent) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        binding = ViewCardLegacyImageBinding.inflate(LayoutInflater.from(context), this, true);
        if (parent instanceof VerticalGridView)
            setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        else
            setLayoutParams(new LayoutParams(WRAP_CONTENT, MATCH_PARENT));

        isGridActivity = (context instanceof GenericGridActivity);

        imageView = binding.cardImage;
        mTextSizeSP = (int) ContextExtensionsKt.getRawDimension(context, R.dimen.card_text_size_primary);
        // calc min/max card sizes
        int screenDiagonal = Utils.calcDiagonal(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);
        MAX_DIAGONAL = (int) (screenDiagonal * 0.4); // 40%
        MIN_DIAGONAL = (int) (screenDiagonal * 0.11); // 11%

        // card defaults
        setCardElevation(0);
        setMaxCardElevation(0);
        setUseCompatPadding(false);
        setPreventCornerOverlap(false);
//        setContentPadding(4,4,4,4);
//        setClipChildren(false);

        setOnFocusChangeListener(this::onFocusChange);

        var colorBG = userPreferences.getValue().get(UserPreferences.Companion.getCardColorBG());
        setCardBackgroundColor(colorBG.getColorValue());
        if (ColorSelectionBG.TRANSPARENT.equals(colorBG)) {
            TextViewExtensionKt.setShadow(binding.titleText, 3f);
            TextViewExtensionKt.setShadow(binding.contentText, 3f);
        }

        var focusColorValue = userPreferences.getValue().get(UserPreferences.Companion.getFocusBorderColor()).getColorValue();
        if (focusColorValue == 0) {
            focusColorValue = ContextExtensionsKt.getColorFromAttribute(context, android.R.attr.colorAccent);
        } else {
            // desaturate focus color
            float[] hsv = new float[3];
            Color.colorToHSV(focusColorValue, hsv);
            if (hsv[0] > 0) {
                hsv[1] = 0.8f;
                focusColorValue = Color.HSVToColor(255, hsv);
            }
        }
        var cardRadius = Utils.convertDpToPixel(context, userPreferences.getValue().get(UserPreferences.Companion.getCardCornerRounding()));
        setRadius(cardRadius);

        var focusBorderSize = userPreferences.getValue().get(UserPreferences.Companion.getFocusBorderSize());
        Drawable focusDrawable = null;
        if (focusBorderSize != FocusBorderSize.NONE) {
            if (infoType == CardInfoType.UNDER_MINI || infoType == CardInfoType.UNDER_FULL)
                focusDrawable = DrawableBuilder.INSTANCE.createFocusBorderDrawable(focusBorderSize, focusColorValue, cardRadius, true);
            else
                focusDrawable = DrawableBuilder.INSTANCE.createFocusBorderDrawable(focusBorderSize, focusColorValue, cardRadius, false);
        }

        if (focusBorderSize != FocusBorderSize.NONE && !(infoType == CardInfoType.OVERLAY || infoType == CardInfoType.OVERLAY_ICON)) {
            binding.resumeProgress.setProgressDrawable(DrawableBuilder.INSTANCE.createFocusProgressDrawable(context, focusColorValue));
        } else {
            binding.resumeProgress.setProgressDrawable(AppCompatResources.getDrawable(context, R.drawable.progress_bar_card));
        }

        switch (infoType) {
            case NO_INFO -> {
                binding.cardImage.setForeground(focusDrawable);
            }
            case OVERLAY -> {
                binding.cardLayout.setForeground(focusDrawable);
                binding.infoOverlayLayout.setVisibility(VISIBLE);
            }
            case OVERLAY_ICON -> {
                binding.cardLayout.setForeground(focusDrawable);
                binding.infoOverlayLayout.setVisibility(VISIBLE);
                binding.infoOverlayIcon.setVisibility(VISIBLE);
                binding.infoOverlayCountText.setVisibility(INVISIBLE); // needed for text center symmetry
            }
            case UNDER_MINI -> {
                binding.cardImage.setForeground(focusDrawable);
                binding.titleText.setVisibility(VISIBLE);
                Utils.setViewPaddingBottom(binding.titleText, Utils.convertDpToPixel(context, 2));
            }
            case UNDER_FULL -> {
                binding.cardImage.setForeground(focusDrawable);
                binding.titleText.setVisibility(VISIBLE);
                binding.contentText.setVisibility(VISIBLE);
            }
        }

        if (userPreferences.getValue().get(UserPreferences.Companion.getFocusIconSize()) == FocusIconSize.NONE)
            binding.focusIconOverlayLayout.setVisibility(GONE);

        // prevent wobbly scrolling text
        final int textFlag = Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.LINEAR_TEXT_FLAG;
        binding.titleText.setPaintFlags(textFlag);
        binding.contentText.setPaintFlags(textFlag);
        //        binding.badgeText.setPaintFlags(textFlag);
        //        binding.watchedOverlayText.setPaintFlags(textFlag);
        binding.infoOverlayText.setPaintFlags(textFlag);
        //        binding.infoOverlayCountText.setPaintFlags(textFlag);

        // "hack" to trigger KeyProcessor to open the menu for this item on long press
        setOnLongClickListener(v -> {
            Activity activity = ContextExtensionsKt.getActivity(context);
            if (activity == null) return false;
            // Make sure the view is focused so the created menu uses it as anchor
            if (!v.requestFocus()) return false;
            return activity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MENU));
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //        Timber.d("Card.onMeasure: W<%s> H<%s>", MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (height <= 0 && width > 0)
            height = (int) (width / getImageAspect()); // not 100% accurate info text is ignored
        if (width <= 0 && height > 0)
            width = (int) (height * getImageAspect()); // not 100% accurate info text is ignored

        if (width > 0 && height > 0)
            adaptLayoutSizes(width, height);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected double getImageAspect() {
        var lp = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
        return Utils.parseDimensionRatioString(lp.dimensionRatio);
    }

    private void adaptLayoutSizes(int viewWidth, int viewHeight) {
        if (mHeight == viewHeight && mWidth == viewWidth)
            return;

        mWidth = viewWidth;
        mHeight = viewHeight;

        int maxTextSizeSP = Utils.clamp(Utils.convertPixelToSP(getContext(), viewHeight * 0.12f), 10, 18); // max 18% of height
        int diagonal = Utils.calcDiagonal(viewWidth, viewHeight);
        int textSizeSP = Math.min(Math.round(Utils.lerp(10, 18, diagonal, MIN_DIAGONAL, MAX_DIAGONAL)), maxTextSizeSP);
        if (textSizeSP != mTextSizeSP) {
            mTextSizeSP = textSizeSP;
//            Timber.w("adaptTextSizes: SP<%s>", mTextSizeSP);
            binding.titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeSP);
            binding.contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeSP - 1);
            binding.infoOverlayText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeSP);
            binding.infoOverlayCountText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeSP);
        }

        int viewHeightDP = Utils.convertPixelToDP(getContext(), viewHeight);
        int maxIconSizeDP = Math.min(Math.round(viewHeightDP * 0.94f), userPreferences.getValue().get(UserPreferences.Companion.getFocusIconSize()).getMaxSizeDP());
        if (maxIconSizeDP > 0) {
            int minIconSizeDP = Math.max(20, Math.round(maxIconSizeDP / 2.5f));
            ViewLayoutExtensionKt.setSizeDP(binding.mainOverlayIcon, Utils.lerp(minIconSizeDP, maxIconSizeDP, diagonal, MIN_DIAGONAL, MAX_DIAGONAL));
            ViewLayoutExtensionKt.setSizeDP(binding.mainOverlayIconBg, Utils.lerp(minIconSizeDP, maxIconSizeDP, diagonal, MIN_DIAGONAL, MAX_DIAGONAL));
        }
        ViewLayoutExtensionKt.setSizeDP(binding.badgeStatusLayout      , Utils.lerp(16, 40, diagonal, MIN_DIAGONAL, MAX_DIAGONAL));
        ViewLayoutExtensionKt.setSizeDP(binding.favIcon                , Utils.lerp(16, 40, diagonal, MIN_DIAGONAL, MAX_DIAGONAL));
        ViewLayoutExtensionKt.setSizeDP(binding.watchedOverlayText     , Utils.lerp(16, 40, diagonal, MIN_DIAGONAL, MAX_DIAGONAL));
        ViewLayoutExtensionKt.setHeightDP(binding.bannerOverlayImage   , Utils.lerp(28, 80, diagonal, MIN_DIAGONAL, MAX_DIAGONAL));
        ViewLayoutExtensionKt.setHeightDP(binding.topBadgeLayout       , Utils.lerp(14, 24, diagonal, MIN_DIAGONAL, MAX_DIAGONAL));

        ViewLayoutExtensionKt.setHeightDP(binding.resumeProgress       , Utils.lerp(4, 8, diagonal, MIN_DIAGONAL, MAX_DIAGONAL));
    }

    public void setBanner(int bannerResource) {
        binding.bannerOverlayImage.setImageDrawable(ContextCompat.getDrawable(getContext(), bannerResource));
        binding.bannerOverlayImage.setVisibility(VISIBLE);
    }

    public final AsyncImageView getMainImageView() {
        return binding.cardImage;
    }

    public void setPlayingIndicator(boolean playing) {
        if (playing)
            setStatusBadge(ContextCompat.getDrawable(getContext(), R.drawable.ic_play), null, ContextExtensionsKt.getDrawableFromAttribute(getContext(), R.attr.accentShape));
        else
            setStatusBadge(null, null, null);
    }

    public void setStatusBadge(@Nullable Drawable icon, @Nullable String text, @Nullable Drawable iconBackground) {
        binding.badgeStatusLayout.setVisibility((icon != null || text != null) ? VISIBLE : GONE);

        binding.badgeStatusImage.setVisibility(icon != null ? VISIBLE : GONE);
        binding.badgeStatusImage.setBackground(iconBackground);
        binding.badgeStatusImage.setImageDrawable(icon);

        binding.badgeStatusText.setText(text);
        binding.badgeStatusText.setVisibility(text != null ? VISIBLE : GONE);
    }

    public void setRating(@Nullable BaseRowItem rowItem, @NonNull RatingType ratingType) {
        String text = null;
        Drawable image = null;
        if (ratingType != RatingType.RATING_HIDDEN && rowItem != null && rowItem.getBaseItem() != null && rowItem.getBaseItemType() != BaseItemType.UserView) {
            BaseItemDto baseItem = rowItem.getBaseItem();
            if (baseItem.getBaseItemType() == BaseItemType.Movie && baseItem.getCriticRating() != null && (ratingType == RatingType.RATING_TOMATOES || rowItem.getBaseItem().getCommunityRating() == null)) {
                text = String.format(Locale.US, "%.0f%%", baseItem.getCriticRating());
                image = rowItem.getBadgeImage(getContext());
            } else if (baseItem.getCommunityRating() != null) {
                text = String.format(Locale.US, "%.1f", rowItem.getBaseItem().getCommunityRating());
                image = ContextCompat.getDrawable(getContext(), R.drawable.ic_star_stroke);
            }
        }

        binding.topBadgeLayout.setVisibility(image != null ? VISIBLE : GONE);
        binding.badgeImage.setVisibility(image != null ? VISIBLE : GONE);
        binding.badgeImage.setImageDrawable(image);

        binding.badgeText.setText(text);
        binding.badgeText.setVisibility(text != null ? VISIBLE : GONE);
    }

    public void setMainImageAspect(final double aspect) {
        double checkedAspect = Utils.clamp(aspect, ASPECT_RATIO_POSTER, ASPECT_RATIO_BANNER);
        if (aspect != checkedAspect)
            Timber.w("setMainImageAspect fixed Aspect:<%s> to <%s>", aspect, checkedAspect);

        var lp = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
        double aspectCard = Utils.parseDimensionRatioString(lp.dimensionRatio);
        if (!Utils.equalsDelta(aspectCard, checkedAspect, 0.01)) {
            //        layoutParams.dimensionRatio = "2:3";
            //            layoutParams.dimensionRatio = "W," + checkedAspect;
            lp.dimensionRatio = Double.toString(checkedAspect);
            lp.height = MATCH_CONSTRAINT;
            lp.width = MATCH_CONSTRAINT;
            imageView.setLayoutParams(lp);
//            postInvalidate();
//            Timber.w("new Aspect:<%s>", lp.dimensionRatio);
        }
    }

    private void setInfoOverlayCount(@NonNull BaseRowItem rowItem) {
        if (rowItem.getBaseItem() != null) {
            var item = rowItem.getBaseItem();
            Integer count = null;
            switch (rowItem.getBaseItemType()) {
                case BoxSet, ChannelFolderItem, CollectionFolder, Folder, MovieGenreFolder, MusicGenreFolder, PhotoAlbum, Playlist, Series -> count = item.getChildCount();
                case Movie, Video ->
                        count = item.getMediaSourceCount() != null ? item.getMediaSourceCount() : item.getMediaSources() != null ? item.getMediaSources().size() : null;
                case Season -> count = item.getEpisodeCount();
            }

            if (count != null && count > 1) {
                binding.infoOverlayCountText.setVisibility(VISIBLE);
                binding.infoOverlayCountText.setText(nf.format(count));
            } else {
                binding.infoOverlayCountText.setVisibility(GONE);
            }
        }
    }

    public void setInfoOverlayData(@NonNull BaseRowItem rowItem) {
        setInfoOverlayCount(rowItem);
        binding.infoOverlayIcon.setImageDrawable(rowItem.getTypeIconImage(getContext()));
    }

    public void setTitleText(CharSequence text) {
        binding.titleText.setText(text);
        binding.infoOverlayText.setText(text);
    }

    public void setContentText(CharSequence text) {
        binding.contentText.setText(text);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setUnwatchedCount(int count) {
        if (count > 0) {
            if (count > 9) {
                binding.watchedOverlayText.setPadding(2, 2, 2, 2);
            }
            binding.watchedOverlayText.setText(count > 99 ? getContext().getString(R.string.watch_count_overflow) : nf.format(count));
            binding.watchedOverlayText.setForeground(null);
            binding.watchedOverlayText.setVisibility(VISIBLE);
        } else if (count == 0) {
            binding.watchedOverlayText.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.ic_watch));
            binding.watchedOverlayText.setText("");
            binding.watchedOverlayText.setVisibility(VISIBLE);
        } else {
            binding.watchedOverlayText.setVisibility(GONE);
        }
    }

    public void setProgress(int pct) {
        if (pct > 0) {
            binding.resumeProgress.setProgress(pct);
            binding.resumeProgress.setVisibility(VISIBLE);
        } else {
            binding.resumeProgress.setVisibility(GONE);
        }
    }

    public void showFavIcon(boolean show) {
        binding.favIcon.setVisibility(show ? VISIBLE : GONE);
    }

    public void resetCard() {
        //        Timber.d("resetCard");
        binding.cardImage.setImageDrawable(null);
        //        Glide.with(getContext()).clear(binding.mainImage);

        binding.bannerOverlayImage.setVisibility(GONE);
        //        binding.bannerOverlayImage.setImageDrawable(null);

        binding.badgeStatusLayout.setVisibility(GONE);
        binding.badgeStatusText.setVisibility(GONE);
        binding.badgeStatusImage.setVisibility(GONE);
        //        binding.badgeStatusImage.setImageDrawable(null);
        binding.favIcon.setVisibility(GONE);

        binding.watchedOverlayText.setVisibility(GONE);
        binding.watchedOverlayText.setForeground(null);
        binding.watchedOverlayText.setText("");

        binding.resumeProgress.setVisibility(GONE);

        binding.topBadgeLayout.setVisibility(GONE);
        //        binding.badgeImage.setImageDrawable(null);
        binding.badgeText.setText("");

        //        binding.infoOverlayIcon.setImageDrawable(null);
        binding.infoOverlayCountText.setText("");
        binding.infoOverlayCountText.setVisibility(GONE);

        setTitleText("");
        setContentText("");
    }

    public void setFocusIcon(@NonNull BaseRowItem rowItem) {
        binding.mainOverlayIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_more));
        if (rowItem.isBaseItem() && rowItem.getBaseItemType() != null) {
            switch (rowItem.getBaseItemType()) {
                case AggregateFolder, UserView, ChannelFolderItem, CollectionFolder, Folder, MovieGenreFolder, MusicGenreFolder -> {
                    binding.mainOverlayIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_folder));
                    binding.mainOverlayIcon.setPadding(6, 6, 6, 6); // FIXME folder icon needs more spacing
                }
                case BoxSet, Channel, ChannelVideoItem, Episode, LiveTvChannel, Movie, MusicAlbum, MusicVideo, Photo, Recording, Season, Series, Trailer, TvChannel, Video -> {
                    binding.mainOverlayIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_play));
                }
            }
        } else if (BaseRowItem.ItemType.Chapter.equals(rowItem.getItemType())) {
            binding.mainOverlayIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_play));
        }
    }

    protected void onFocusChange(View v, boolean hasFocus) {
        setSelected(hasFocus); // select view so Text can scroll
        if (userPreferences.getValue().get(UserPreferences.Companion.getFocusIconSize()) != FocusIconSize.NONE) {
            binding.focusIconOverlayLayout.setVisibility(hasFocus ? VISIBLE : INVISIBLE);
        }
    }
}