package cd.infoset.smaprtpay.merchant.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cd.infoset.smaprtpay.merchant.ui.FlexPayChip
import cd.infoset.smaprtpay.merchant.ui.theme.FlexPayTheme

@Composable
internal fun AmountAndTransactionTypeCard(
    modifier: Modifier = Modifier,
    selectedTransactionType: TransactionType,
    onSelectedTransactionTypeChange: (TransactionType) -> Unit,
    onOtherServicesClicked: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "100", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "USD", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Rounded.ExpandMore, contentDescription = null)
                }
            }


            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    FlexPayChip(
                        selected = selectedTransactionType == TransactionType.TRANSFER,
                        onClick = { onSelectedTransactionTypeChange(TransactionType.TRANSFER) },
                        label = "Money transfer"
                    )
                }
                item {
                    FlexPayChip(
                        selected = selectedTransactionType == TransactionType.REQUEST_PAYMENT,
                        onClick = { onSelectedTransactionTypeChange(TransactionType.REQUEST_PAYMENT) },
                        label = "Request Payment",
                    )
                }
                item {
                    FlexPayChip(
                        selected = selectedTransactionType == TransactionType.PAYMENT,
                        onClick = { onSelectedTransactionTypeChange(TransactionType.PAYMENT) },
                        label = "Payment",
                    )
                }
                item {
                    AssistChip(
                        onClick = onOtherServicesClicked,
                        label = { Text(text = "Other services") },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AmountAndTransactionTypeCardPreview() {
    FlexPayTheme {
        AmountAndTransactionTypeCard(
            selectedTransactionType = TransactionType.REQUEST_PAYMENT,
            onSelectedTransactionTypeChange = {},
            onOtherServicesClicked = {}
        )
    }
}