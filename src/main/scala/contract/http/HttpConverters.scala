package contract.http

import org.asynchttpclient.{ListenableFuture, Param}
import scala.concurrent.{ExecutionContext, ExecutionException, Future, Promise}
import scala.util.{Failure, Try}

object HttpConverters {

  implicit class SeqToParams(val params: Seq[(String, String)]) {

    import scala.collection.JavaConverters._

    def asParams: java.util.List[Param] = params.map { case (key, value) =>
      new Param(key, value)
    }.toList.asJava
  }

  implicit class AsyncHttpClientFutureOps[T](whenResponse: ListenableFuture[T]) {

    def asynchronously(implicit ec: ExecutionContext): Future[T] = {
      val promise = Promise[T]()

      val onResponseCallback = new Runnable {
        override def run(): Unit = {
          val response = Try(whenResponse.get).recoverWith {
            case ex: ExecutionException => Failure(ex.getCause)
          }
          promise.complete(response)
        }
      }

      whenResponse.addListener(onResponseCallback, (command: Runnable) => ec.execute(command))
      promise.future
    }
  }

}