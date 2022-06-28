package cd.shuri.smaprtpay.merchant.screens.transaction

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import cd.shuri.smaprtpay.merchant.network.TransactionResponse

class TransactionsPagingSource(
    private val transactionType: String,
    private val auth: String,
    private val customer: String
) : PagingSource<Int, TransactionResponse>(){
    override fun getRefreshKey(state: PagingState<Int, TransactionResponse>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionResponse> {
        return try {
            val nextPageNumber = params.key ?: 1
            val transactions = SmartPayApi.smartPayApiService.getTransactionByTypeAsync(
                authorization = auth,
                type = transactionType,
                customer = customer,
                nextPage = nextPageNumber
            )
            LoadResult.Page(
                data = transactions,
                prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                nextKey = if (transactions.isEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}