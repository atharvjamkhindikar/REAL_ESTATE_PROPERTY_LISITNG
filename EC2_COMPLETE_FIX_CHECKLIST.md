# ✅ EC2 COMPLETE FIX SUMMARY

## Your Problem
```
ERROR: GET http://localhost:8080/api/api/properties/available 
net::ERR_CONNECTION_REFUSED
```

## Root Cause
- Frontend running on EC2 trying to call `localhost:8080`
- `localhost` on EC2 = the EC2 machine itself
- Frontend can't reach itself as a backend!
- Solution: Use EC2's **public IP or domain name**

---

## 4 Files to Update

### 1️⃣ WebConfig.java (Backend)
**Location:** `src/main/java/com/realestate/config/WebConfig.java`

**Status:** ✅ Already fixed in your attached file

**What to do:** Add your EC2 IP/domain

```java
.allowedOrigins(
    "http://localhost:3000",                    // For local dev
    "http://3.91.60.245",                       // YOUR EC2 IP (replace!)
    "http://ec2-3-91-60-245.compute-1.amazonaws.com"  // YOUR EC2 DOMAIN
)
```

**To apply:**
1. Go to WebConfig.java
2. Find `.allowedOrigins(`
3. Add your EC2 IP and domain
4. Save file
5. Rebuild: `mvn clean package`

---

### 2️⃣ Create api.js (Frontend Service)
**Location:** `src/services/api.js` (NEW FILE - create it!)

**What to do:** Copy this code:

```javascript
import axios from 'axios';

// Auto-detect environment and set base URL
const getBaseURL = () => {
  const hostname = window.location.hostname;
  
  if (hostname === 'localhost' || hostname === '127.0.0.1') {
    return 'http://localhost:8080/api';  // Local dev
  }
  
  return `http://${hostname}:8080/api`;  // EC2 or production
};

const API_BASE_URL = getBaseURL();

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' }
});

// PROPERTY SERVICE
export const propertyService = {
  getAvailableProperties: (page = 0, size = 10) => 
    api.get(`/properties/available?page=${page}&size=${size}`),
  getAllProperties: () => 
    api.get('/properties'),
  getPropertyById: (id) => 
    api.get(`/properties/${id}`),
  createProperty: (data) => 
    api.post('/properties', data),
  updateProperty: (id, data) => 
    api.put(`/properties/${id}`, data),
  deleteProperty: (id) => 
    api.delete(`/properties/${id}`)
};

// AUTH SERVICE
export const authService = {
  register: (data) => 
    api.post('/auth/register', data),
  login: (data) => 
    api.post('/auth/login', data),
  agentLogin: (data) => 
    api.post('/auth/agent-login', data)
};

// FAVORITES SERVICE
export const favoriteService = {
  getFavorites: (userId) => 
    api.get(`/favorites?userId=${userId}`),
  addFavorite: (userId, propertyId, notes = '') => 
    api.post('/favorites', { userId, propertyId, notes }),
  removeFavorite: (userId, propertyId) => 
    api.delete('/favorites', { params: { userId, propertyId } })
};

// BUILDER GROUP SERVICE
export const builderGroupService = {
  getAllBuilderGroups: () => 
    api.get('/builder-groups'),
  getBuilderGroupById: (id) => 
    api.get(`/builder-groups/${id}`)
};

export default {
  propertyService,
  authService,
  favoriteService,
  builderGroupService
};
```

**To apply:**
1. Create new file: `src/services/api.js`
2. Paste the code above
3. Save

---

### 3️⃣ Update PropertyList.js (Frontend Component)
**Location:** `src/components/PropertyList.js`

**Before (❌ WRONG):**
```javascript
useEffect(() => {
  fetch('http://localhost:8080/api/properties/available')
    .then(res => res.json())
    .then(data => setProperties(data));
}, []);
```

**After (✅ CORRECT):**
```javascript
import { propertyService } from '../services/api';

const PropertyList = () => {
  useEffect(() => {
    propertyService.getAvailableProperties(0, 10)
      .then(res => {
        console.log('✅ Properties loaded:', res.data);
        setProperties(res.data.data || res.data);
      })
      .catch(err => {
        console.error('❌ Error:', err.message);
        setError(err.message);
      });
  }, []);

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

**To apply:**
1. Open PropertyList.js
2. Import propertyService at top:
   ```javascript
   import { propertyService } from '../services/api';
   ```
3. Replace fetch/axios calls with propertyService
4. Save

---

### 4️⃣ Update All Other Components
**Find all files that call the API:**

Search for: `localhost:8080` or `fetch(` or `axios.get(`

Files to update:
- [ ] PropertyDetail.js
- [ ] Favorites.js
- [ ] Register.js
- [ ] Login.js
- [ ] AuthController.js (if exists)
- [ ] Any other component calling API

**Replace all with:**
```javascript
import { propertyService, authService, favoriteService } from '../services/api';

// Then use the service
propertyService.getPropertyById(id)
authService.login(credentials)
favoriteService.addFavorite(userId, propertyId)
```

---

## Deployment Steps

### Step 1: Update Backend Config
```bash
cd D:\CDAC Project\Atharva\Atharva\real-estate-backend

# Edit WebConfig.java with your EC2 IP
# Then rebuild
mvn clean package
```

### Step 2: Create Frontend API Service
```bash
cd D:\CDAC Project\Atharva\Atharva\real-estate-frontend

# Create src/services/api.js (paste code from above)
# Update all components to use it
```

### Step 3: Rebuild & Deploy on EC2
```bash
# SSH into EC2
ssh -i your-key.pem ec2-user@YOUR_EC2_IP

# Stop services
pm2 stop all

# Update backend
cd real-estate-backend
git pull  # if using git
mvn clean package -DskipTests
java -jar target/real-estate-backend-*.jar &

# Update frontend
cd ../real-estate-frontend
git pull  # if using git
npm install
npm run build
npm start &

# Or restart with PM2
pm2 restart all
```

---

## Testing

### ✅ Test 1: Backend Responding
```bash
curl http://YOUR_EC2_IP:8080/api/properties
# Should return JSON with properties
```

### ✅ Test 2: Frontend Connected
1. Open browser: `http://YOUR_EC2_IP:3000`
2. Press F12 (DevTools)
3. Go to Console tab
4. Go to Network tab
5. Refresh page (F5)
6. Check network requests:
   - Should see `http://YOUR_EC2_IP:8080/api/properties/available`
   - Status: 200 OK ✅
   - NO CORS errors
7. Properties should display

### ✅ Test 3: No Double /api
- Network tab should show: `/api/properties/available`
- NOT: `/api/api/properties/available` ❌

---

## Your EC2 Details (Get from AWS)

AWS Console → EC2 → Instances → Your Instance

```
Public IPv4 address: ____________________
Public IPv4 DNS: ____________________
```

**Use these values in:**
1. WebConfig.java → allowedOrigins
2. Browser address bar: `http://IP:3000`

---

## Troubleshooting

### Still getting localhost error?
1. Check api.js exists at `src/services/api.js`
2. Check all components import from api.js
3. Rebuild frontend: `npm run build`
4. Clear browser cache: Ctrl+Shift+Delete

### CORS error?
1. Check EC2 IP added to WebConfig.java
2. Restart backend: kill and restart Java process
3. Clear browser cache

### Connection refused?
1. Verify backend running: `curl http://EC2_IP:8080/api/properties`
2. Check EC2 security group allows port 8080 inbound
3. Verify using correct EC2 IP (not localhost)

### Double /api in URL?
1. Check baseURL in api.js ends with `/api`
2. Component calls should be: `api.get('/properties/available')`
3. NOT: `api.get('/api/properties/available')`

---

## Summary Checklist

**Backend:**
- [ ] WebConfig.java updated with EC2 IP/domain
- [ ] CORS allows port 3000 (frontend) and EC2 domain
- [ ] Rebuilt: `mvn clean package`
- [ ] Running on port 8080

**Frontend:**
- [ ] Created src/services/api.js
- [ ] baseURL configured correctly
- [ ] All components import propertyService
- [ ] NO hardcoded localhost:8080 URLs
- [ ] Rebuilt: `npm run build`
- [ ] Running on port 3000

**Testing:**
- [ ] Backend responds: `curl http://EC2_IP:8080/api/properties`
- [ ] Frontend loads: `http://EC2_IP:3000`
- [ ] Network tab shows correct URLs (http://EC2_IP:8080/api/...)
- [ ] ✅ NO CORS errors
- [ ] ✅ Data loads
- [ ] ✅ NO double /api in URLs

---

## Quick Reference

```
BEFORE (❌):
- Frontend calls: http://localhost:8080/api/properties
- Error: ERR_CONNECTION_REFUSED
- URL becomes: http://localhost:8080/api/api/properties

AFTER (✅):
- Frontend calls: http://EC2_IP:8080/api/properties
- Result: 200 OK
- Properties load successfully
```

---

## Next Steps

1. Get your EC2 IP from AWS Console
2. Update WebConfig.java with EC2 IP
3. Create src/services/api.js with code above
4. Update PropertyList.js and other components
5. Rebuild both backend and frontend
6. Redeploy on EC2
7. Test at http://EC2_IP:3000

See detailed guides:
- `EC2_QUICK_FIX_GUIDE.md` - Step-by-step
- `EC2_FRONTEND_BACKEND_CONNECTION_FIX.md` - Complete guide
- `EC2_CONNECTION_VISUAL_GUIDE.md` - Visual explanation

