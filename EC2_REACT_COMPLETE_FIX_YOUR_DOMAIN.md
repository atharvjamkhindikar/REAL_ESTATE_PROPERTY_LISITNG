# ✅ REACT FIX - Complete Instructions for Your EC2 Instance

## Your EC2 Details
```
Public Domain: ec2-13-220-57-64.compute-1.amazonaws.com
Backend URL: http://ec2-13-220-57-64.compute-1.amazonaws.com:8080
Frontend URL: http://ec2-13-220-57-64.compute-1.amazonaws.com:3000
```

---

## What's Been Done ✅

**Backend - WebConfig.java:**
```java
✅ CORS configured for:
   - localhost:3000, localhost:3001 (local dev)
   - ec2-13-220-57-64.compute-1.amazonaws.com (production)
✅ All HTTP methods enabled (GET, POST, PUT, DELETE, PATCH, OPTIONS)
✅ Credentials allowed
✅ Ready to accept requests from React
```

---

## What You Need to Do - React Frontend

### Step 1: Create `.env` File

**Location:** Root of your React project (same folder as package.json)

**Filename:** `.env`

**Content:**
```
REACT_APP_API_URL=http://ec2-13-220-57-64.compute-1.amazonaws.com:8080
```

**Save this file!**

---

### Step 2: Create API Service File

**Location:** `src/services/api.js`

**Create this folder if it doesn't exist:** `src/services/`

**Copy this entire code:**

```javascript
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL.endsWith('/api') ? API_BASE_URL : `${API_BASE_URL}/api`,
  headers: {
    'Content-Type': 'application/json'
  }
});

// PROPERTY SERVICE
export const propertyService = {
  getAvailableProperties: (page = 0, size = 10) => 
    api.get(`/properties/available?page=${page}&size=${size}`),
  
  getAllProperties: () => 
    api.get('/properties'),
  
  getPropertyById: (id) => 
    api.get(`/properties/${id}`),
  
  getPropertyDetails: (id, userId = null) => {
    const params = userId ? `?userId=${userId}` : '';
    return api.get(`/properties/${id}/details${params}`);
  },
  
  createProperty: (data) => 
    api.post('/properties', data),
  
  updateProperty: (id, data) => 
    api.put(`/properties/${id}`, data),
  
  deleteProperty: (id) => 
    api.delete(`/properties/${id}`),
  
  searchProperties: (filters) => 
    api.post('/properties/search', filters),
  
  getPropertyImages: (propertyId) => 
    api.get(`/properties/${propertyId}/images`),
  
  addPropertyImage: (propertyId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(`/properties/${propertyId}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },
  
  deletePropertyImage: (imageId) => 
    api.delete(`/properties/images/${imageId}`)
};

// AUTH SERVICE
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

// FAVORITES SERVICE
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

// BUILDER GROUP SERVICE
export const builderGroupService = {
  getAllBuilderGroups: () => 
    api.get('/builder-groups'),
  
  getBuilderGroupById: (id) => 
    api.get(`/builder-groups/${id}`),
  
  uploadBuilderGroupImage: (groupId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(`/builder-groups/${groupId}/image`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  }
};

// SUBSCRIPTION SERVICE
export const subscriptionService = {
  getSubscriptions: (page = 0, size = 10) => 
    api.get(`/subscriptions?page=${page}&size=${size}`),
  
  createSubscription: (userId, planType) => 
    api.post('/subscriptions', { userId, planType }),
  
  getUserSubscription: (userId) => 
    api.get(`/subscriptions/user/${userId}`),
  
  cancelSubscription: (subscriptionId) => 
    api.delete(`/subscriptions/${subscriptionId}`)
};

export default {
  propertyService,
  authService,
  favoriteService,
  builderGroupService,
  subscriptionService
};
```

---

### Step 3: Update All React Components

**Find all files calling the API:**

Search for `localhost:8080` or `fetch(` or `axios.get(` in your `src/` folder:

```bash
# In your React project terminal
grep -r "localhost:8080" src/
```

**Common files to update:**
- PropertyList.js / PropertyPage.js
- PropertyDetail.js
- Favorites.js
- Register.js / RegistrationForm.js
- Login.js / LoginForm.js
- Dashboard.js / AgentDashboard.js
- UpgradePlan.js
- BuilderGroupFilter.js
- SubscriptionManagement.js
- Any other component with API calls

**For each file:**

#### Add this import at the top:
```javascript
import { propertyService, authService, favoriteService, builderGroupService, subscriptionService } from '../services/api';
```

#### Replace all fetch/axios calls:

**BEFORE (❌ WRONG):**
```javascript
// Hardcoded localhost
fetch('http://localhost:8080/api/properties/available')
  .then(res => res.json())
  .then(data => setProperties(data));

// Or with axios
axios.get('http://localhost:8080/api/properties/available')
  .then(res => setProperties(res.data));
```

**AFTER (✅ CORRECT):**
```javascript
// Using the service
propertyService.getAvailableProperties()
  .then(res => {
    console.log('✅ Properties loaded:', res.data);
    setProperties(res.data.data || res.data);
  })
  .catch(err => {
    console.error('❌ Error:', err.message);
    setError(err.message);
  });
```

#### Example Replacement in PropertyList.js:

**BEFORE:**
```javascript
useEffect(() => {
  const fetchProperties = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/properties/available');
      const data = await response.json();
      setProperties(data);
    } catch (error) {
      setError(error);
    }
  };
  fetchProperties();
}, []);
```

**AFTER:**
```javascript
import { propertyService } from '../services/api';

useEffect(() => {
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
  fetchProperties();
}, []);
```

---

### Step 4: Rebuild & Deploy

```bash
# In your React project directory on EC2

# 1. Install dependencies
npm install

# 2. Rebuild the app
npm run build

# 3. Start the app
npm start
```

Or if using PM2:
```bash
pm2 restart all
# or
pm2 stop frontend
pm2 start npm --name "frontend" -- start
```

---

## Testing

### ✅ Test 1: Backend is Running
```bash
# From EC2 terminal
curl http://ec2-13-220-57-64.compute-1.amazonaws.com:8080/api/properties
# Should return JSON with properties
```

### ✅ Test 2: Open React in Browser
```
Go to: http://ec2-13-220-57-64.compute-1.amazonaws.com:3000
```

### ✅ Test 3: Check Browser Console
```
Press F12 → Go to Console tab
```

**Look for:**
- ✅ NO error messages
- ✅ See "✅ Properties loaded:" message
- ✅ Data displayed on page

### ✅ Test 4: Check Network Tab
```
F12 → Network tab → Refresh page
```

**Look for:**
- ✅ Requests to `http://ec2-13-220-57-64.compute-1.amazonaws.com:8080/api/...`
- ✅ Response status: 200 OK
- ✅ Response shows JSON data
- ✅ NO "double /api/api" in URLs
- ✅ NO CORS errors

---

## All Files to Update (Checklist)

Search for `localhost:8080` in your project:

```bash
grep -r "localhost:8080" src/
```

**Common files:**
- [ ] src/components/PropertyList.js
- [ ] src/components/PropertyDetail.js
- [ ] src/components/PropertyPage.js
- [ ] src/components/Favorites.js
- [ ] src/pages/Register.js
- [ ] src/pages/Login.js
- [ ] src/pages/Dashboard.js
- [ ] src/pages/AgentDashboard.js
- [ ] src/pages/UpgradePlan.js
- [ ] src/components/BuilderGroupFilter.js
- [ ] src/components/SubscriptionManagement.js
- [ ] Any other component with fetch/axios calls

---

## Troubleshooting

### ❌ Still Getting "Connection Refused"

**Solution:**
1. Stop React: `Ctrl+C`
2. Verify .env file exists and has correct EC2 domain
3. Delete any build cache: `rm -rf node_modules/.cache`
4. Reinstall: `npm install`
5. Start again: `npm start`
6. Clear browser cache: `Ctrl+Shift+Delete`

### ❌ Still Seeing `localhost` in Network Tab

**Solution:**
1. Search for ALL `localhost:8080` occurrences:
   ```bash
   grep -r "localhost:8080" src/
   ```
2. Replace ALL with `propertyService` calls
3. Rebuild: `npm run build`
4. Start: `npm start`

### ❌ Properties Not Loading

**Solution:**
1. Check backend is running:
   ```bash
   curl http://ec2-13-220-57-64.compute-1.amazonaws.com:8080/api/properties
   ```
2. Check .env file has correct domain
3. Check browser console (F12) for exact error
4. Check Network tab (F12) for failed requests

### ❌ CORS Error (but curl works)

**Solution:**
1. Verify WebConfig.java has your EC2 domain
2. Backend is already updated ✅
3. Clear browser cache
4. Try incognito window

---

## Success Indicators

You'll know it worked when:

✅ Browser loads: `http://ec2-13-220-57-64.compute-1.amazonaws.com:3000`
✅ Console shows: "✅ Properties loaded: [...]"
✅ Network tab shows: Calls to `http://ec2-13-220-57-64.compute-1.amazonaws.com:8080/api/...`
✅ API responses: Status 200
✅ Properties display on page
✅ NO "Connection refused" error
✅ NO "CORS" error

---

## Summary of Changes

| Component | Change | Status |
|-----------|--------|--------|
| WebConfig.java | Added EC2 domain to CORS | ✅ Done |
| .env (React) | Create with EC2 domain | 📝 You do this |
| src/services/api.js | Create API service | 📝 You do this |
| React components | Replace localhost calls | 📝 You do this |
| Build & Deploy | npm run build && npm start | 📝 You do this |

---

## Next Steps

1. ✅ Backend CORS updated (done)
2. 📝 Create `.env` with EC2 domain
3. 📝 Create `src/services/api.js` with service code
4. 📝 Update all React components
5. 📝 Rebuild: `npm run build`
6. 📝 Start: `npm start`
7. 📝 Test: Open browser and check

---

## Quick Command List

```bash
# Check for localhost hardcoding
grep -r "localhost:8080" src/

# Rebuild
npm install
npm run build

# Start development
npm start

# Or with PM2
pm2 restart frontend

# Test backend
curl http://ec2-13-220-57-64.compute-1.amazonaws.com:8080/api/properties
```

---

**You're almost there!** Just update the React code and rebuild. The backend is ready to go! 🚀

