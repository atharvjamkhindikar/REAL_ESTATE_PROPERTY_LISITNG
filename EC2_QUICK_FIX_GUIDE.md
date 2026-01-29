# ⚡ EC2 QUICK FIX - Frontend-Backend Connection (Same Instance)

## Your Situation
✅ Frontend on EC2 (port 3000)
✅ Backend on EC2 (port 8080)
✗ Frontend can't connect to backend
✗ Error: `net::ERR_CONNECTION_REFUSED` on `localhost:8080`

---

## The Problem (In Simple Terms)

Your React app is running on EC2 and trying to call `http://localhost:8080/api/properties/available`

But `localhost` on EC2 means "the EC2 machine itself", and the frontend can't be the backend!

**Fix:** Use the **EC2's public IP or domain name** instead of localhost

---

## 3 Quick Steps to Fix

### Step 1: Find Your EC2 Public Details

Go to AWS Console:
1. EC2 Dashboard → Instances
2. Find your instance
3. Copy the **Public IPv4 address** (e.g., `3.91.60.245`)
4. OR copy **Public IPv4 DNS** (e.g., `ec2-3-91-60-245.compute-1.amazonaws.com`)

### Step 2: Update Backend CORS (WebConfig.java)

The file is already partially updated. Now add your EC2 IP:

```java
registry.addMapping("/**")
    .allowedOrigins(
        "http://localhost:3000",                    // If you dev locally
        "http://localhost:3001",                    // If you dev locally
        "http://ec2-3-91-60-245.compute-1.amazonaws.com",  // ← YOUR EC2 DOMAIN
        "http://3.91.60.245"                       // ← YOUR EC2 IP (replace this!)
    )
```

### Step 3: Update Frontend API Service

Create `src/services/api.js` in your React project:

```javascript
import axios from 'axios';

// Get the EC2 IP or domain from environment
// Replace '3.91.60.245' with YOUR EC2 IP
const API_BASE_URL = `http://${window.location.hostname}:8080/api`;

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' }
});

export const propertyService = {
  getAvailableProperties: (page = 0, size = 10) => 
    api.get(`/properties/available?page=${page}&size=${size}`),
  
  getAllProperties: () => 
    api.get('/properties'),
  
  getPropertyById: (id) => 
    api.get(`/properties/${id}`)
};

export default propertyService;
```

---

## How to Use in Your Components

**Before (❌ WRONG):**
```javascript
// PropertyList.js
fetch('http://localhost:8080/api/properties/available')
```

**After (✅ CORRECT):**
```javascript
// PropertyList.js
import { propertyService } from '../services/api';

useEffect(() => {
  propertyService.getAvailableProperties(0, 10)
    .then(response => console.log(response.data))
    .catch(error => console.error(error));
}, []);
```

---

## Rebuild & Deploy

```bash
# On your EC2 instance

# Stop current services
pm2 stop all

# Rebuild backend
cd real-estate-backend
mvn clean package
java -jar target/real-estate-backend-*.jar &

# Rebuild frontend
cd ../real-estate-frontend
npm install
npm run build
npm start &

# Or use PM2
pm2 start npm --name "frontend" -- start
pm2 start java --name "backend" -- -jar real-estate-backend-*.jar
```

---

## Test It Works

1. Open browser: `http://YOUR_EC2_IP:3000`
2. Press F12 (DevTools)
3. Go to Console tab
4. Check Network tab
5. Should see API calls to `http://YOUR_EC2_IP:8080/api/...`
6. ✅ NO CORS errors
7. ✅ Properties load

---

## If Still Not Working

### Check 1: Backend is Running
```bash
curl http://YOUR_EC2_IP:8080/api/properties
# Should return JSON, not error
```

### Check 2: Security Group
AWS → EC2 → Security Groups → Your group
- Inbound rules should allow:
  - Port 3000 (Frontend)
  - Port 8080 (Backend)

### Check 3: Environment Variables
Create `.env` file in React root:
```
REACT_APP_API_URL=http://YOUR_EC2_IP:8080/api
```

---

## Complete Template for api.js

If you want a complete, production-ready api.js:

**Save this as `src/services/api.js`:**

```javascript
import axios from 'axios';

// Auto-detect environment
const getBaseURL = () => {
  const hostname = window.location.hostname;
  
  // If running on EC2 (not localhost)
  if (hostname !== 'localhost' && hostname !== '127.0.0.1') {
    return `http://${hostname}:8080/api`;
  }
  
  // Local development
  return 'http://localhost:8080/api';
};

const API_BASE_URL = getBaseURL();

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Property API
export const propertyService = {
  getAvailableProperties: (page = 0, size = 10) => 
    api.get(`/properties/available?page=${page}&size=${size}`),
  getAllProperties: () => api.get('/properties'),
  getPropertyById: (id) => api.get(`/properties/${id}`),
  createProperty: (data) => api.post('/properties', data),
  updateProperty: (id, data) => api.put(`/properties/${id}`, data),
  deleteProperty: (id) => api.delete(`/properties/${id}`)
};

// Auth API
export const authService = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  agentLogin: (data) => api.post('/auth/agent-login', data)
};

// Favorites API
export const favoriteService = {
  getFavorites: (userId) => api.get(`/favorites?userId=${userId}`),
  addFavorite: (userId, propertyId) => 
    api.post('/favorites', { userId, propertyId }),
  removeFavorite: (userId, propertyId) => 
    api.delete('/favorites', { params: { userId, propertyId } })
};

export default { propertyService, authService, favoriteService };
```

---

## Summary

✅ Create `api.js` with correct base URL
✅ Update WebConfig.java CORS with your EC2 IP
✅ Use propertyService in all components
✅ Rebuild both apps
✅ Test on EC2 IP, not localhost

**Next step:** Follow the detailed guide in `EC2_FRONTEND_BACKEND_CONNECTION_FIX.md`

