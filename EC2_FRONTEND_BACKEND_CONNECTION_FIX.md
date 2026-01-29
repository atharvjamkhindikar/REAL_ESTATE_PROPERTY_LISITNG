# 🔧 EC2 Instance - Frontend-Backend Connection Fix

## Problem Summary

**Error in Console:**
```
GET http://localhost:8080/api/api/properties/available?page=0&size=10 net::ERR_CONNECTION_REFUSED
```

**Issues:**
1. ✗ Double `/api/api` in URL (should be `/api`)
2. ✗ Frontend calling `localhost:8080` which doesn't exist on EC2
3. ✗ Backend is running on EC2, not local machine
4. ✗ CORS not configured for EC2 domain

**Solution:**
Since frontend and backend are on the **same EC2 instance**, use relative paths OR the EC2's public IP/domain.

---

## Fix Step 1: Update CORS Configuration

**File:** `src/main/java/com/realestate/config/WebConfig.java`

✅ Already updated (see attached)

Now add your EC2 domain:

```java
registry.addMapping("/**")
    .allowedOrigins(
        "http://localhost:3000",           // Local dev
        "http://localhost:3001",           // Local dev
        "http://127.0.0.1:3000",          // Local dev
        "http://127.0.0.1:3001",          // Local dev
        "http://ec2-3-91-60-245.compute-1.amazonaws.com",  // ← UPDATE THIS
        "http://YOUR_EC2_PUBLIC_IP:3000"  // ← ADD THIS
    )
    .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
    .allowedHeaders("*")
    .allowCredentials(true)
    .maxAge(3600);
```

**Replace with your actual EC2 details:**
- `ec2-3-91-60-245.compute-1.amazonaws.com` → Your EC2 public domain
- `YOUR_EC2_PUBLIC_IP` → Your EC2 public IP (e.g., `3.91.60.245`)

---

## Fix Step 2: Update Frontend API Configuration

**Best Option:** Create `src/services/api.js` in your React project:

```javascript
import axios from 'axios';

// Detect environment and set base URL
const getBaseURL = () => {
  // Production (EC2)
  if (window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') {
    return `http://${window.location.hostname}:8080/api`;
  }
  // Development (Local)
  return 'http://localhost:8080/api';
};

const API_BASE_URL = getBaseURL();

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Property Service
export const propertyService = {
  // Get available properties with pagination
  getAvailableProperties: (page = 0, size = 10) => 
    api.get(`/properties/available?page=${page}&size=${size}`),
  
  // Get all properties
  getAllProperties: () => 
    api.get('/properties'),
  
  // Get property by ID
  getPropertyById: (id) => 
    api.get(`/properties/${id}`),
  
  // Get property details with images
  getPropertyDetails: (id, userId = null) => {
    const params = userId ? `?userId=${userId}` : '';
    return api.get(`/properties/${id}/details${params}`);
  },
  
  // Create property
  createProperty: (data) => 
    api.post('/properties', data),
  
  // Update property
  updateProperty: (id, data) => 
    api.put(`/properties/${id}`, data),
  
  // Delete property
  deleteProperty: (id) => 
    api.delete(`/properties/${id}`),
  
  // Search properties
  searchProperties: (filters) => 
    api.post('/properties/search', filters),
  
  // Get property images
  getPropertyImages: (propertyId) => 
    api.get(`/properties/${propertyId}/images`),
  
  // Add property image
  addPropertyImage: (propertyId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(`/properties/${propertyId}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },
  
  // Delete property image
  deletePropertyImage: (imageId) => 
    api.delete(`/properties/images/${imageId}`)
};

// Auth Service
export const authService = {
  register: (userData) => 
    api.post('/auth/register', userData),
  
  login: (credentials) => 
    api.post('/auth/login', credentials),
  
  agentLogin: (credentials) => 
    api.post('/auth/agent-login', credentials),
  
  getCurrentUser: (userId) => 
    api.get(`/auth/me/${userId}`)
};

// Favorites Service
export const favoriteService = {
  getFavorites: (userId) => 
    api.get(`/favorites?userId=${userId}`),
  
  addFavorite: (userId, propertyId, notes = '') => 
    api.post('/favorites', { userId, propertyId, notes }),
  
  removeFavorite: (userId, propertyId) => 
    api.delete('/favorites', {
      params: { userId, propertyId }
    }),
  
  isFavorite: (userId, propertyId) => 
    api.get(`/favorites/check?userId=${userId}&propertyId=${propertyId}`)
};

// Builder Group Service
export const builderGroupService = {
  getAllBuilderGroups: () => 
    api.get('/builder-groups'),
  
  getBuilderGroupById: (id) => 
    api.get(`/builder-groups/${id}`)
};

// Export default for convenience
export default {
  propertyService,
  authService,
  favoriteService,
  builderGroupService
};
```

---

## Fix Step 3: Update Your React Components

**Example: PropertyList.js**

```javascript
import { propertyService } from '../services/api';

const PropertyList = () => {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchProperties();
  }, []);

  const fetchProperties = async () => {
    try {
      setLoading(true);
      const response = await propertyService.getAvailableProperties(0, 10);
      console.log('✅ Properties fetched:', response.data);
      setProperties(response.data.data || response.data);
      setError(null);
    } catch (err) {
      console.error('❌ Error fetching properties:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      {properties.map(prop => (
        <div key={prop.id}>{prop.title}</div>
      ))}
    </div>
  );
};

export default PropertyList;
```

---

## Fix Step 4: Backend CORS Configuration

**File:** `src/main/java/com/realestate/config/WebConfig.java`

**Update with EC2 domain:**

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    // Local development
                    "http://localhost:3000",
                    "http://localhost:3001",
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:3001",
                    
                    // EC2 Production (REPLACE WITH YOUR ACTUAL DOMAIN/IP)
                    "http://ec2-3-91-60-245.compute-1.amazonaws.com",
                    "http://3.91.60.245"  // Replace with your EC2 public IP
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

---

## Fix Step 5: Rebuild and Deploy

**On EC2 Instance:**

```bash
# Backend
cd /path/to/real-estate-backend
mvn clean package
java -jar target/real-estate-backend-*.jar

# Frontend (in another terminal)
cd /path/to/real-estate-frontend
npm install
npm run build
npm start
```

Or if using Docker/PM2, restart services:
```bash
pm2 restart backend
pm2 restart frontend
```

---

## Verification Checklist

### ✅ Backend Setup
- [ ] Backend is running on EC2 (`localhost:8080` or `:8080`)
- [ ] CORS configured for EC2 domain
- [ ] All endpoints working at `http://EC2_IP:8080/api/...`

### ✅ Frontend Setup
- [ ] Frontend is running on EC2 at port 3000/3001
- [ ] `api.js` created with correct base URL
- [ ] Components use `propertyService` from api.js
- [ ] NO hardcoded `localhost:8080` URLs

### ✅ Testing
- [ ] Open frontend on EC2: `http://EC2_IP:3000`
- [ ] Check console (F12) - NO CORS errors
- [ ] Check network tab - API calls to `http://EC2_IP:8080/api/...`
- [ ] Properties load without errors

---

## Environment Variables (Better Approach)

Create `.env` file in React root:

```
REACT_APP_API_URL=http://ec2-3-91-60-245.compute-1.amazonaws.com:8080/api
```

Then in `api.js`:

```javascript
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';
```

For different environments, create:
- `.env` → Production defaults
- `.env.local` → Local overrides
- `.env.development` → Dev specific

---

## Common Issues & Solutions

### Issue 1: Still getting `localhost:8080` error
**Solution:** 
- Check if api.js exists and is imported
- Verify base URL is correct
- Rebuild frontend: `npm run build`

### Issue 2: CORS error on EC2
**Solution:**
- Add EC2 domain to CORS origins in WebConfig
- Restart backend after changes
- Clear browser cache (Ctrl+Shift+Delete)

### Issue 3: Double `/api/api` in URL
**Solution:**
- Check endpoint in component: `api.get('/properties/...')`
- Base URL should be `http://EC2_IP:8080/api`
- Then endpoint becomes `/api/properties/...`

### Issue 4: Connection refused
**Solution:**
- Verify backend is running: `curl http://EC2_IP:8080/api/properties`
- Check EC2 security group allows port 8080 (inbound)
- Verify frontend using correct EC2 IP/domain

---

## Test Commands

```bash
# Test backend is running
curl http://EC2_IP:8080/api/properties

# Test CORS
curl -H "Origin: http://EC2_IP:3000" \
     -H "Access-Control-Request-Method: GET" \
     -X OPTIONS \
     http://EC2_IP:8080/api/properties

# Expected: Access-Control-Allow-Origin header present
```

---

## Summary

✅ Create `src/services/api.js` with correct base URL
✅ Update all components to use `propertyService`
✅ Configure CORS for EC2 domain in WebConfig
✅ Rebuild and redeploy both apps
✅ Test with EC2 IP/domain, not localhost

