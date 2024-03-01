package cd.infoset.smaprtpay.merchant.screens.payment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cd.infoset.smaprtpay.merchant.R
import cd.infoset.smaprtpay.merchant.screens.payment.components.PaymentModeUIPicker
import cd.infoset.smaprtpay.merchant.ui.theme.FlexPayTheme
import cd.infoset.smaprtpay.merchant.ui.theme.SingUpTextField
import cd.infoset.smaprtpay.merchant.utilities.toogle

enum class PaymentMode {
    Card,
    Mobile
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PaymentScreen(
    onNavBac: () -> Unit = {},
    onNavigateToTapToPhone: () -> Unit = {}
) {
    var reasonForPayment by remember {
        mutableStateOf("")
    }

    var reference by remember {
        mutableStateOf("")
    }

    var showPaymentMode by remember {
        mutableStateOf(false)
    }

    var selectedPaymentMode by remember {
        mutableStateOf(PaymentMode.Card)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Payment request") },
                navigationIcon = {
                    IconButton(onClick = onNavBac ) {
                        Icon(painter = painterResource(id = R.drawable.arrow_back), contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(color = MaterialTheme.colorScheme.surface)

            ) {
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(color = MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1000",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "USD",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                SingUpTextField(
                    value = reasonForPayment,
                    onValueChange = { reasonForPayment = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Reason for payment") }
                )

                SingUpTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Reference") }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable {
                            showPaymentMode = showPaymentMode.toogle()
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select a payment mode",
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                AnimatedVisibility(visible = showPaymentMode) {
                    PaymentModeUIPicker(selectedPaymentMode) {
                        selectedPaymentMode = it
                    }
                }
            }

            Button(
                onClick = onNavigateToTapToPhone,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Next")
            }
        }
    }
}


@Preview
@Composable
private fun PaymentScreenPreview() {
    FlexPayTheme {
        PaymentScreen()
    }
}
