package cd.infoset.smaprtpay.merchant.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cd.infoset.smaprtpay.merchant.R

@Composable
internal fun AdCard(
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.padding(all = 8.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.home_illustration),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(216.dp)
                        .padding(start = 16.dp),
                    contentScale = ContentScale.Fit
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(220.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                )
            }

            Text(
                text = "Pay with FlexPay",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}