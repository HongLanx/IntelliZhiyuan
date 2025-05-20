package com.intelli.zhiyuan.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.intelli.zhiyuan.R

@Composable
fun BottomNav(selectedTab: String,
              onTabSelected: (String) -> Unit,
              ) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(id = R.string.home)
                )
            },
            selected = selectedTab == "home",
            onClick = { onTabSelected("home")
                }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(id = R.string.profile)
                )
            },
            selected = selectedTab == "profile",
            onClick = { onTabSelected("profile")
                }
        )
    }
}
