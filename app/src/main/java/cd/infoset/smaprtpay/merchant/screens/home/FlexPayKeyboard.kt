package cd.infoset.smaprtpay.merchant.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cd.infoset.smaprtpay.merchant.ui.theme.FlexPayTheme

@Composable
internal fun FlexPayKeyBoard(
    modifier: Modifier = Modifier,
    onKeyClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        KeyboardLine(
            numbers = listOf("1", "2", "3"),
            onKeyClick = onKeyClick,
            onDeleteClick = onDeleteClick
        )

        KeyboardLine(
            numbers = listOf("4", "5", "6"),
            onKeyClick = onKeyClick,
            onDeleteClick = onDeleteClick
        )

        KeyboardLine(
            numbers = listOf("7", "8", "9"),
            onKeyClick = onKeyClick,
            onDeleteClick = onDeleteClick
        )

        KeyboardLine(
            numbers = listOf(".", "0", "<="),
            onKeyClick = onKeyClick,
            onDeleteClick = onDeleteClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KeyboardLine(
    numbers: List<String>,
    onKeyClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        numbers.forEach { number ->
            ElevatedCard(
                onClick = {
                    if (number == "<=") {
                        onDeleteClick()
                    } else {
                        onKeyClick(number)
                    }
                },
                modifier = Modifier
                    .width(80.dp)
                    .height(44.dp),
                colors = CardDefaults.elevatedCardColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (number == "<=") {
                        Icon(
                            imageVector = Icons.Outlined.Backspace,
                            contentDescription = null,
                            modifier = Modifier.size(size = 16.dp)
                        )
                    } else {
                        Text(
                            text = number,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun FlexPayKeyBoardPreview() {
    FlexPayTheme {
        FlexPayKeyBoard(
            onKeyClick = {},
            onDeleteClick = {}
        )
    }
}