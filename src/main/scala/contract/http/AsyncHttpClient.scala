package contract.http

import contract.http.HttpConverters.{AsyncHttpClientFutureOps, SeqToParams}
import org.asynchttpclient.{DefaultAsyncHttpClient, DefaultAsyncHttpClientConfig, Response}

import scala.concurrent.{ExecutionContext, Future}

//class AsyncHttpClient(implicit executionContext: ExecutionContext) extends AutoCloseable {

//  private val httpClient = new DefaultAsyncHttpClient(
//    new DefaultAsyncHttpClientConfig.Builder()
//      .setConnectTimeout(3 * 1000)
//      .setRequestTimeout(3 * 1000)
//      .build())
//
//  sys.addShutdownHook(close())
//
//  def doGet(url: String, queryParams: Seq[(String, String)] = Nil): Future[Response] =
//    httpClient
//      .prepareGet(url)
//      .addQueryParams(queryParams.asParams)
//      .execute()
//      .asynchronously
//
//  override def close(): Unit = httpClient.close()
//}
