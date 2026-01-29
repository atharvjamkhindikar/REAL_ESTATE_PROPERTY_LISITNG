# CORS Configuration Reference

## Summary of Changes

### Problem
The React frontend running on `http://localhost:3000` was unable to make API calls to the Spring Boot backend running on `http://localhost:8080` due to CORS (Cross-Origin Resource Sharing) policy violations.

Error message:
```
Access to XMLHttpRequest at 'http://localhost:8080/api/properties/available?page=0&size=10' 
from origin 'http://localhost:3000' has been blocked by CORS policy: 
Response to preflight request doesn't pass access control check: 
No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

## Solution Overview

Implemented dual-layer CORS configuration:
1. **Global CORS Config** (CorsConfig.java) - Applies to all endpoints
2. **Controller-level CORS** (@CrossOrigin annotations) - Explicit per-controller configuration

## Detailed Configuration

### Layer 1: Global Configuration (CorsConfig.java)

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        // Allowed origins
                        .allowedOrigins(
                            "http://localhost:3000",           // Local frontend (port 3000)
                            "http://localhost:3001",           // Alternative local port
                            "http://ec2-3-91-60-245.compute-1.amazonaws.com"  // AWS deployment
                        )
                        // Allowed HTTP methods
                        .allowedMethods(
                            "GET",      // Retrieve data
                            "POST",     // Create data
                            "PUT",      // Update data (full)
                            "PATCH",    // Update data (partial)
                            "DELETE",   // Delete data
                            "OPTIONS"   // Preflight requests
                        )
                        // Allowed headers
                        .allowedHeaders("*")
                        // Allow cookies/credentials
                        .allowCredentials(true)
                        // Cache preflight for 1 hour
                        .maxAge(3600);
            }
        };
    }
}
```

### Layer 2: Controller-level Configuration

#### PropertyController
```java
@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://ec2-3-91-60-245.compute-1.amazonaws.com"
})
public class PropertyController { ... }
```

**Affects endpoints**:
- GET `/api/properties`
- GET `/api/properties/available`
- GET `/api/properties/{id}`
- POST `/api/properties`
- PUT `/api/properties/{id}`
- DELETE `/api/properties/{id}`
- And all other property-related endpoints

#### AuthController
```java
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://ec2-3-91-60-245.compute-1.amazonaws.com"
})
public class AuthController { ... }
```

**Affects endpoints**:
- POST `/api/auth/register`
- POST `/api/auth/login`
- POST `/api/auth/agent-login`
- GET `/api/auth/me/{id}`

#### ContactAgentController
```java
@RestController
@RequestMapping("/api/contact-agents")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://ec2-3-91-60-245.compute-1.amazonaws.com"
})
public class ContactAgentController { ... }
```

#### BuilderGroupController
```java
@RestController
@RequestMapping("/api/builder-groups")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://ec2-3-91-60-245.compute-1.amazonaws.com"
})
public class BuilderGroupController { ... }
```

#### SubscriptionController (Already had CORS)
```java
@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class SubscriptionController { ... }
```

#### FavoriteController (Already had CORS)
```java
@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class FavoriteController { ... }
```

#### PropertyImageController (Already had CORS)
```java
@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PropertyImageController { ... }
```

## How CORS Works

### Preflight Request (OPTIONS)
When the browser makes a cross-origin request:

1. **Browser sends OPTIONS request**:
```
OPTIONS /api/properties/available HTTP/1.1
Host: localhost:8080
Origin: http://localhost:3000
Access-Control-Request-Method: GET
Access-Control-Request-Headers: content-type
```

2. **Server responds with CORS headers**:
```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Headers: *
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

3. **Browser allows actual request**:
```
GET /api/properties/available HTTP/1.1
Host: localhost:8080
Origin: http://localhost:3000
```

## Configuration Hierarchy

1. **Most Specific**: Controller-level @CrossOrigin annotations
2. **Medium**: Global CorsConfig configuration
3. **Least Specific**: Default Spring Security settings

The most specific configuration takes precedence.

## Supported Origins

| Origin | Purpose | Status |
|--------|---------|--------|
| `http://localhost:3000` | Local development (primary) | ✅ Supported |
| `http://localhost:3001` | Local development (alternative) | ✅ Supported |
| `http://ec2-3-91-60-245.compute-1.amazonaws.com` | AWS EC2 production | ✅ Supported |

## Supported Methods

| Method | Purpose | Status |
|--------|---------|--------|
| GET | Retrieve resources | ✅ Supported |
| POST | Create resources | ✅ Supported |
| PUT | Replace resources | ✅ Supported |
| PATCH | Partial updates | ✅ Supported |
| DELETE | Delete resources | ✅ Supported |
| OPTIONS | CORS preflight | ✅ Supported |

## Testing the Configuration

### Browser Console Test
```javascript
// Test from browser console at http://localhost:3000
fetch('http://localhost:8080/api/properties/available?page=0&size=10')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

### cURL Test
```bash
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: GET" \
     -X OPTIONS \
     http://localhost:8080/api/properties/available \
     -v
```

Look for response headers:
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
```

## Files Modified

1. ✅ `src/main/java/com/realestate/config/CorsConfig.java` - Global CORS
2. ✅ `src/main/java/com/realestate/config/WebConfig.java` - Removed duplicate config
3. ✅ `src/main/java/com/realestate/controller/PropertyController.java` - Added @CrossOrigin
4. ✅ `src/main/java/com/realestate/controller/AuthController.java` - Updated @CrossOrigin
5. ✅ `src/main/java/com/realestate/controller/ContactAgentController.java` - Updated @CrossOrigin
6. ✅ `src/main/java/com/realestate/controller/BuilderGroupController.java` - Updated @CrossOrigin

## Troubleshooting

### If CORS errors persist:

1. **Clear browser cache**:
   - Ctrl+Shift+Delete (Windows)
   - Cmd+Shift+Delete (Mac)
   - Or use Incognito mode

2. **Restart backend**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **Check browser console**:
   - F12 → Console tab
   - Look for CORS-related errors

4. **Check network requests**:
   - F12 → Network tab
   - Look for OPTIONS requests
   - Check response headers for CORS headers

5. **Verify backend URL**:
   - Ensure API calls use correct protocol (http not https)
   - Ensure API calls use correct hostname:port (localhost:8080)

## Production Considerations

For production deployment on AWS:
- Update frontend origin to actual AWS CloudFront/domain URL
- Consider using environment variables for origin URLs
- Avoid `allowedOrigins("*")` in production (less secure)
- Use specific, whitelisted origins only

## References

- [Spring Boot CORS Documentation](https://spring.io/guides/gs/rest-service-cors/)
- [MDN CORS Documentation](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Spring Security CORS Configuration](https://spring.io/blog/2015/06/08/cors-support-in-spring-framework)
