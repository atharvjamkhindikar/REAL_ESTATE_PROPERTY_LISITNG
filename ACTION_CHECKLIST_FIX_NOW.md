# ✅ ACTION CHECKLIST - Fix React Connection Now

## Get Your EC2 Details (2 minutes)

- [ ] Open AWS Console
- [ ] Go to EC2 → Instances
- [ ] Find your instance
- [ ] Copy **Public IPv4 address**: `_________________`
- [ ] Copy **Public IPv4 DNS**: `_________________`

**Example:** `3.91.60.245`

---

## Step 1: Create .env File (1 minute)

**Location:** Root of your React project (same folder as package.json)

**Filename:** `.env`

**Content:**
```
REACT_APP_API_URL=http://YOUR_EC2_IP:8080
```

**Example:**
```
REACT_APP_API_URL=http://3.91.60.245:8080
```

- [ ] Created .env file
- [ ] Entered correct EC2 IP
- [ ] Saved file

---

## Step 2: Create API Service (3 minutes)

**Location:** `src/services/api.js` (NEW FILE - create this folder if missing)

**Copy entire code below:**

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

- [ ] Created `src/services/api.js`
- [ ] Pasted code above
- [ ] Saved file

---

## Step 3: Update React Components (5-10 minutes)

### Find all files calling the API:

```bash
# Run this in your React project terminal
grep -r "localhost:8080" src/
```

**Common files:**
- [ ] PropertyList.js
- [ ] PropertyDetail.js
- [ ] Favorites.js
- [ ] Register.js
- [ ] Login.js
- [ ] Dashboard.js
- [ ] Any component with fetch/axios

### For each file:

#### Add import at top:
```javascript
import { propertyService, authService, favoriteService } from '../services/api';
```

#### Replace fetch calls:

**BEFORE:**
```javascript
fetch('http://localhost:8080/api/properties/available')
  .then(res => res.json())
  .then(data => setProperties(data));
```

**AFTER:**
```javascript
propertyService.getAvailableProperties()
  .then(res => setProperties(res.data.data || res.data))
  .catch(err => console.error('Error:', err));
```

#### Replace axios calls:

**BEFORE:**
```javascript
axios.get('http://localhost:8080/api/properties/available')
```

**AFTER:**
```javascript
propertyService.getAvailableProperties()
```

- [ ] Updated PropertyList.js
- [ ] Updated PropertyDetail.js
- [ ] Updated Favorites.js
- [ ] Updated Register.js
- [ ] Updated Login.js
- [ ] Updated other API-calling components

---

## Step 4: Rebuild React App (2 minutes)

```bash
# In React project directory
cd path/to/react/project

# Reinstall dependencies
npm install

# Rebuild
npm run build

# Start development server
npm start
```

- [ ] Ran `npm install`
- [ ] Ran `npm run build`
- [ ] Ran `npm start`
- [ ] React app started without errors

---

## Step 5: Test in Browser (2 minutes)

### Open Browser:
```
http://YOUR_EC2_IP:3000
```

**Example:** `http://3.91.60.245:3000`

- [ ] Opened correct URL in browser

### Check Console (F12):
```
Press F12 → Go to Console tab
```

- [ ] No error messages
- [ ] See "✅ Properties loaded:" message
- [ ] OR see data in Network tab requests

### Check Network Tab:
```
F12 → Network tab → Refresh page
```

- [ ] See API calls to `http://YOUR_EC2_IP:8080/api/...`
- [ ] Status: 200 (not 404, not connection error)
- [ ] Response shows JSON data

### Check Page Display:
- [ ] Properties display on page
- [ ] No loading errors
- [ ] Data actually shows

---

## Verification Checklist

### ✅ Before Testing React:

- [ ] Backend running on EC2: `curl http://EC2_IP:8080/api/properties`
- [ ] Gets JSON response (not error)
- [ ] Port 8080 responds

### ✅ During React Testing:

- [ ] .env file exists with correct IP
- [ ] api.js file exists with correct code
- [ ] Components import from api.js
- [ ] No localhost:8080 hardcoded anywhere
- [ ] npm run build completed successfully
- [ ] npm start running without errors

### ✅ After React Testing:

- [ ] Browser shows no console errors
- [ ] Network tab shows correct URLs (EC2 IP, not localhost)
- [ ] API responses status 200 OK
- [ ] Data displays on page
- [ ] No double `/api/api` in URLs

---

## If Something Goes Wrong

### Problem: Still getting Connection Refused

**Try:**
1. Stop React app (Ctrl+C)
2. Delete `.env` and recreate (make sure IP is correct)
3. Run `npm start` again
4. Clear browser cache (Ctrl+Shift+Delete)
5. Try in incognito window

### Problem: Still seeing localhost in Network tab

**Try:**
1. Search for `localhost:8080` in entire src/ folder
2. Replace ALL occurrences with propertyService calls
3. Run `npm run build`
4. Run `npm start`
5. Check Network tab again

### Problem: Still not loading

**Try:**
1. Verify EC2 IP is correct:
   ```bash
   curl http://YOUR_EC2_IP:8080/api/properties
   ```
   Should return JSON

2. Verify .env value:
   - Open `.env`
   - Check exact IP matches AWS Console
   - Save file
   - Restart React

3. Check browser console for exact error message
4. Share error message for detailed help

---

## Summary

| Step | Task | Time | Status |
|------|------|------|--------|
| 1 | Get EC2 IP from AWS | 2 min | [ ] |
| 2 | Create .env file | 1 min | [ ] |
| 3 | Create api.js service | 3 min | [ ] |
| 4 | Update components | 10 min | [ ] |
| 5 | Rebuild React | 2 min | [ ] |
| 6 | Test in browser | 2 min | [ ] |

**Total Time: ~20 minutes**

---

## Success Indicators

You'll know it worked when:

✅ Browser at `http://EC2_IP:3000` loads without errors
✅ Console shows no error messages
✅ Network tab shows `http://EC2_IP:8080/api/...` calls
✅ API responses show 200 status
✅ Properties display on the page
✅ No "Connection refused" error

---

## Need Help?

Check these documents:
1. **REACT_FIX_3_MINUTES.md** - Detailed explanation
2. **CURL_WORKS_REACT_DOESNT_DIAGNOSIS.md** - Why this happens
3. **WHY_CURL_WORKS_REACT_DOESNT_FINAL.md** - Visual summary

**All files are in your project folder!**

