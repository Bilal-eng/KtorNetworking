package com.bilal.ktornetworking.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bilal.ktornetworking.presentation.news_detail.NewsDetailRoute
import com.bilal.ktornetworking.presentation.news_list.NewsListRoute

@Composable
fun NewsNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NewsListDestination) {
        composable<NewsListDestination> {
            NewsListRoute(onNewsClick = { newsId ->
                navController.navigate(NewsDetailDestination(newsId)) {
                    launchSingleTop = true
                }
            })
        }
        composable<NewsDetailDestination> { backStackEntry ->
            val destination = backStackEntry.toRoute<NewsDetailDestination>()
            NewsDetailRoute(
                newsId = destination.newsId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
