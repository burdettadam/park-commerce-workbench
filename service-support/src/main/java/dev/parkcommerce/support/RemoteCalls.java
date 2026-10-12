package dev.parkcommerce.support;
import dev.parkcommerce.core.Failure;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.*;

public final class RemoteCalls {
    private RemoteCalls() {}
    public static RestClient client(RestClient.Builder builder,String baseUrl,String token){
        var http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
        var requests=new JdkClientHttpRequestFactory(http);requests.setReadTimeout(Duration.ofSeconds(5));
        return builder.clone().baseUrl(baseUrl).requestFactory(requests)
            .defaultHeader("Authorization","Bearer "+token).build();
    }
    public static <T> T get(RestClient client,String path,Class<T> type,Object... values){
        try {
            T body=client.get().uri(path,values).retrieve().body(type);
            if(body==null)throw new Failure(Failure.Code.DEPENDENCY_UNAVAILABLE,"Dependency returned no data");
            return body;
        }catch(RestClientResponseException e){
            if(e.getStatusCode().value()==404)throw new Failure(Failure.Code.NOT_FOUND,"Referenced resource was not found");
            throw new Failure(Failure.Code.DEPENDENCY_UNAVAILABLE,"Dependency request failed");
        }catch(RestClientException e){throw new Failure(Failure.Code.DEPENDENCY_UNAVAILABLE,"Dependency unavailable or request timed out");}
    }
}
