package com.my.ownpulltorefreshdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**刷新的基类
 * Created by linchen on 15/12/21.
 *
 *
 * 对于ListView，ScrollView，WebView这三种情况，
 * 他们是否滑动到最顶部或是最底部的实现是不一样的，所以，
 * 在PullToRefreshBase类中需要调用两个抽象方法来判断
 * 当前的位置是否在顶部或底部，而其派生类必须要实现这两个方法。
 * 对于ListView，它滑动到最顶部的条件就是第一个child完全可见并且first postion是0
 * 对于scrollView来说我们可以判断scrollY是否等于0即可
 */
public abstract class PullToRefreshBase <T extends View> {



    /**
     * 判断刷新的View是否滑动到顶部
     *
     * @return true表示已经滑动到顶部，否则false
     */
    protected abstract boolean isReadyForPullDown();

    /**
     * 判断刷新的View是否滑动到底
     *
     * @return true表示已经滑动到底部，否则false
     */
    protected abstract boolean isReadyForPullUp();

    /**
     * 创建可以刷新的View
     *
     * @param context context
     * @param attrs 属性
     * @return View
     */
    protected abstract T createRefreshableView(Context context, AttributeSet attrs);
}
