package com.neuroandroid.pyreader.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.neuroandroid.pyreader.R;
import com.neuroandroid.pyreader.adapter.BooksByCategoryAdapter;
import com.neuroandroid.pyreader.base.BaseFragment;
import com.neuroandroid.pyreader.model.response.BooksByCategory;
import com.neuroandroid.pyreader.mvp.contract.IRankingBookListContract;
import com.neuroandroid.pyreader.mvp.presenter.RankingBookListPresenter;
import com.neuroandroid.pyreader.utils.DividerUtils;
import com.neuroandroid.pyreader.utils.NavigationUtils;
import com.neuroandroid.pyreader.utils.ThemeUtils;
import com.neuroandroid.pyreader.utils.UIUtils;
import com.neuroandroid.pyreader.widget.CustomRefreshHeader;

import butterknife.BindView;

/**
 * Created by NeuroAndroid on 2017/7/5.
 */

public class RankingBookListFragment extends BaseFragment<IRankingBookListContract.Presenter>
        implements IRankingBookListContract.View {
    @BindView(R.id.refresh_layout)
    TwinklingRefreshLayout mRefreshLayout;
    @BindView(R.id.rv_book_list)
    RecyclerView mRvBookList;
    private BooksByCategoryAdapter mBooksByCategoryAdapter;

    private String mRankingId;

    public void setRankingIdAndRequestRanking(String rankingId) {
        this.mRankingId = rankingId;
        mRefreshLayout.startRefresh();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new RankingBookListPresenter(this);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_ranking_book_list;
    }

    @Override
    protected void initView() {
        mRootView.setBackgroundColor(ThemeUtils.getBackgroundColor());

        mRefreshLayout.setHeaderView(new CustomRefreshHeader(mContext));
        mRefreshLayout.setEnableLoadmore(false);

        mRvBookList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvBookList.addItemDecoration(DividerUtils.defaultHorizontalDivider(mContext));
        mBooksByCategoryAdapter = new BooksByCategoryAdapter(mContext, null, R.layout.item_books);
        mBooksByCategoryAdapter.setBookCoverSize(UIUtils.getDimen(R.dimen.y113), UIUtils.getDimen(R.dimen.x150));
        mRvBookList.setAdapter(mBooksByCategoryAdapter);
    }

    @Override
    protected void initListener() {
        mBooksByCategoryAdapter.setOnItemClickListener((holder, position, item) -> NavigationUtils.goToBookDetailPage(mActivity, item.getBookId(), false));
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                getRanking(mRankingId);
            }
        });
    }

    private void getRanking(String rankingId) {
        mPresenter.getRanking(rankingId);
    }

    @Override
    public void showRanking(BooksByCategory booksByCategory) {
        mBooksByCategoryAdapter.replaceAll(booksByCategory.getBooks());
        hideLoading();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        if (mRefreshLayout != null) mRefreshLayout.finishRefreshing();
    }
}
