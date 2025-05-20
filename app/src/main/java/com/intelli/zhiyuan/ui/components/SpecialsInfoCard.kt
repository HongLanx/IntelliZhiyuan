package com.intelli.zhiyuan.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.intelli.zhiyuan.R
import com.intelli.zhiyuan.data.model.specialsinfo.SpecialsInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialsInfoCard(
    special: SpecialsInfo,
    isChinese: Boolean,
    isFavorite:Boolean,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isChinese) special.name_ch else special.name_en,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = special.spcode,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onToggleFavorite,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) { if (isFavorite) {
                    Icon(imageVector = Icons.Outlined.Star, contentDescription = stringResource(R.string.favorite), tint = Color.Yellow)
                } else {
                    Icon(imageVector = Icons.Filled.Star, contentDescription = stringResource(R.string.infavorite), tint = Color.DarkGray)
                }
            }
        }
    }
}