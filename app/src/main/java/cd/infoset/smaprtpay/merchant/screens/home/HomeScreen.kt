package cd.infoset.smaprtpay.merchant.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cd.infoset.smaprtpay.merchant.ui.theme.FlexPayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    onNavigateToLogin: () -> Unit,
) {

    var selectedTransactionType by rememberSaveable {
        mutableStateOf(TransactionType.REQUEST_PAYMENT)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "FlexPay")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                AmountAndTransactionTypeCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    selectedTransactionType = selectedTransactionType,
                    onSelectedTransactionTypeChange = {
                        selectedTransactionType = it
                    },
                    onOtherServicesClicked = {}
                )

                AdCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                FlexPayKeyBoard(
                    modifier = Modifier.fillMaxWidth(),
                    onKeyClick = {}
                ) {

                }

                Button(
                    onClick = onNavigateToLogin
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}

@Preview(device = "id:pixel_6")
@Composable
private fun HomeScreenPreview() {
    FlexPayTheme {
        Surface {
            HomeScreen {}
        }
    }
}