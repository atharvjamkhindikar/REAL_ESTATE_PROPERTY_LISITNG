# ⚡ INSTANT FIX - React to Backend Connection (Curl Works)

## Your Situation
✅ Curl works: `curl http://EC2_IP:8080/api/properties` → JSON response
❌ React fails: Browser calls `http://localhost:8080` → Connection refused

---

## Why This Happens in 10 Seconds

Your **browser** is on your local computer running React. When React calls `localhost:8080`, it's trying to reach your local computer's port 8080, not the EC2 server!

```
Browser (your computer): "Go to localhost:8080"
         ↓
Your computer: "I don't have a backend running here"
         ↓
Error: Connection refused ❌
```

**Fix:** Tell React to use the **EC2 IP**, not localhost

---

## 3-Minute Fix

### Step 1: Get Your EC2 IP
Go to AWS Console → EC2 → Your Instance
```
Copy: Public IPv4 address
Example: 3.91.60.245
```

### Step 2: Create `.env` in React Root
In your React project root, create a file called `.env`:

```
REACT_APP_API_URL=http://3.91.60.245:8080
```

Replace `3.91.60.245` with your actual EC2 IP

### Step 3: Create `src/services/api.js`

Create a new file at `src/services/api.js`:

```javascript
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL.endsWith('/api') ? API_BASE_URL : `${API_BASE_URL}/api`,
  headers: {
    'Content-Type': 'application/json'
  }
});

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

export const authService = {
  register: (data) => 
    api.post('/auth/register', data),
  
  login: (data) => 
    api.post('/auth/login', data),
  
  agentLogin: (data) => 
    api.post('/auth/agent-login', data)
};

export const favoriteService = {
  getFavorites: (userId) => 
    api.get(`/favorites?userId=${userId}`),
  
  addFavorite: (userId, propertyId) => 
    api.post('/favorites', { userId, propertyId }),
  
  removeFavorite: (userId, propertyId) => 
    api.delete('/favorites', { params: { userId, propertyId } })
};

export default {
  propertyService,
  authService,
  favoriteService
};
```

### Step 4: Update PropertyList.js Component

**Find:** Your PropertyList component (wherever it is)

**Add this import at top:**
```javascript
import { propertyService } from '../services/api';
```

**Replace any fetch/axios calls with:**
```javascript
useEffect(() => {
  fetchProperties();
}, []);

const fetchProperties = async () => {
  try {
    const response = await propertyService.getAvailableProperties(0, 10);
    console.log('✅ Properties loaded:', response.data);
    setProperties(response.data.data || response.data);
  } catch (error) {
    console.error('❌ Error:', error.message);
    setError(error.message);
  }
};
```

### Step 5: Rebuild & Test

```bash
# In React project directory
npm install  # Make sure axios is installed
npm run build
npm start
```

Then open:
```
http://YOUR_EC2_IP:3000
```

Check browser console (F12):
- ✅ No errors
- ✅ See "Properties loaded:" message
- ✅ Properties display on page

---

## Complete Before/After

### BEFORE (❌ WRONG)
```javascript
// PropertyList.js
useEffect(() => {
  fetch('http://localhost:8080/api/properties/available')
    .then(res => res.json())
    .then(data => setProperties(data));
}, []);
```

### AFTER (✅ CORRECT)
```javascript
// PropertyList.js
import { propertyService } from '../services/api';

useEffect(() => {
  propertyService.getAvailableProperties()
    .then(res => {
      console.log('✅ Properties loaded:', res.data);
      setProperties(res.data);
    })
    .catch(err => console.error('❌ Error:', err));
}, []);
```

---

## All Components to Update

Search for `localhost:8080` in your React project and update:

```bash
# Find all files with localhost:8080
grep -r "localhost:8080" src/
```

Files to update:
- [ ] PropertyList.js
- [ ] PropertyDetail.js  
- [ ] Favorites.js
- [ ] Register.js
- [ ] Login.js
- [ ] AgentDashboard.js
- [ ] Any other component calling API

**Replace all with propertyService calls**

---

## Test It Step by Step

### ✅ Test 1: Backend is responding
```bash
# From EC2 terminal
curl http://localhost:8080/api/properties
# Should show JSON
```

### ✅ Test 2: EC2 IP is accessible
```bash
# From your computer's terminal
curl http://YOUR_EC2_IP:8080/api/properties
# Should show JSON
```

### ✅ Test 3: Browser can reach EC2
```
Open: http://YOUR_EC2_IP:8080/api/properties
# Should show JSON in browser
```

### ✅ Test 4: React connects to EC2
```
1. Update .env with EC2 IP
2. Create api.js service
3. Update components
4. npm run build && npm start
5. Open http://YOUR_EC2_IP:3000
6. Check browser console (F12)
7. Should see ✅ Properties loaded message
8. Properties should display on page
```

---

## Common Issues & Quick Fixes

### Issue: Still getting "Connection refused"
**Cause:** `.env` file not read
**Fix:** Restart React app after creating `.env`
```bash
npm start
```

### Issue: Still seeing localhost in network tab
**Cause:** Code still has hardcoded `localhost:8080`
**Fix:** 
1. Search for `localhost:8080` in src/
2. Replace with propertyService calls
3. Rebuild: `npm run build`

### Issue: Properties not loading
**Cause:** Wrong API URL in .env
**Fix:** 
1. Verify EC2 IP in .env: `http://3.91.60.245:8080`
2. Test manually: `curl http://3.91.60.245:8080/api/properties`
3. Check browser console for exact error

### Issue: CORS error (but backend working)
**Cause:** Probably using wrong domain in CORS config
**Fix:** Backend already has CORS for localhost, but may need EC2 domain
```java
// WebConfig.java
.allowedOrigins(
    "http://YOUR_EC2_IP",           // Add this
    "http://localhost:3000"          // Already has this
)
```

---

## TLDR (Too Long, Didn't Read)

1. **Get EC2 IP** from AWS Console (e.g., `3.91.60.245`)
2. **Create `.env`**: `REACT_APP_API_URL=http://3.91.60.245:8080`
3. **Create `src/services/api.js`** with code above
4. **Replace all `localhost:8080` calls** with `propertyService.` calls
5. **Rebuild:** `npm run build && npm start`
6. **Test:** Open `http://3.91.60.245:3000` in browser
7. **Done!** Properties should now load ✅

---

## Why Curl Worked

```
Curl command runs on EC2 server
    ↓
Uses localhost (the EC2 machine itself)
    ↓
Backend on same machine
    ↓
Works! ✅
```

## Why React Doesn't Work (Until Fixed)

```
React in browser on your computer
    ↓
Calls localhost (your computer)
    ↓
No backend on your computer
    ↓
Fails! ❌
```

## After Fix

```
React in browser points to EC2 IP
    ↓
EC2 receives request
    ↓
Backend on EC2 responds
    ↓
Works! ✅
```

