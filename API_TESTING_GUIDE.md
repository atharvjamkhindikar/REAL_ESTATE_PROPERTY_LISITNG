# API Testing Guide - New Features

Quick testing commands for all new features using curl or Postman.

## Base URL
```
http://localhost:8080/api
```

---

## 1. User Registration

### Register as BUYER
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phone": "555-1234567",
    "userType": "BUYER"
  }'
```

### Register as AGENT
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.agent@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phone": "555-7654321",
    "userType": "AGENT",
    "licenseNumber": "RE-12345",
    "company": "Elite Properties",
    "bio": "10 years of real estate experience"
  }'
```

---

## 2. Agent Login

```bash
curl -X POST http://localhost:8080/api/auth/agent-login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane.agent@example.com",
    "password": "password123"
  }'
```

---

## 3. Builder Groups Management

### Get All Builder Groups
```bash
curl -X GET http://localhost:8080/api/builder-groups
```

### Get Active Builder Groups
```bash
curl -X GET http://localhost:8080/api/builder-groups/active
```

### Create Builder Group
```bash
curl -X POST http://localhost:8080/api/builder-groups \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Shapoorji Pallonji",
    "description": "Luxury residential developments across major cities",
    "active": true
  }'
```

### Create Another Builder
```bash
curl -X POST http://localhost:8080/api/builder-groups \
  -H "Content-Type: application/json" \
  -d '{
    "name": "TCG",
    "description": "The Confederation Group - Premium properties",
    "active": true
  }'
```

### Get Specific Builder Group
```bash
curl -X GET http://localhost:8080/api/builder-groups/1
```

### Update Builder Group
```bash
curl -X PUT http://localhost:8080/api/builder-groups/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Shapoorji Pallonji Updated",
    "description": "Updated description",
    "active": true
  }'
```

---

## 4. Add Property with Builder Group

When creating a property, include builder group:
```bash
curl -X POST http://localhost:8080/api/properties \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Luxury Apartment in Bandra",
    "description": "3BHK luxury apartment with sea view",
    "price": 5000000,
    "address": "123 Luxury Lane",
    "city": "Mumbai",
    "state": "Maharashtra",
    "zipCode": "400050",
    "propertyType": "APARTMENT",
    "listingType": "FOR_SALE",
    "bedrooms": 3,
    "bathrooms": 2,
    "squareFeet": 2500,
    "yearBuilt": 2020,
    "available": true,
    "ownerId": 1,
    "builderGroupId": 1
  }'
```

### Search Properties by Builder Group
```bash
curl -X GET "http://localhost:8080/api/properties/search?builderGroupId=1&page=0&size=10"
```

---

## 5. Add Images to Property

### Add Image to Property
```bash
curl -X POST http://localhost:8080/api/properties/5/images \
  -H "Content-Type: application/json" \
  -d '{
    "imageUrl": "https://example.com/property-image-1.jpg",
    "caption": "Living room with panoramic windows",
    "isPrimary": true,
    "displayOrder": 0
  }'
```

### Add Second Image
```bash
curl -X POST http://localhost:8080/api/properties/5/images \
  -H "Content-Type: application/json" \
  -d '{
    "imageUrl": "https://example.com/property-image-2.jpg",
    "caption": "Master bedroom",
    "isPrimary": false,
    "displayOrder": 1
  }'
```

### Get All Images for Property
```bash
curl -X GET http://localhost:8080/api/properties/5/images
```

### Set Primary Image
```bash
curl -X PATCH http://localhost:8080/api/properties/5/images/2/primary
```

### Update Image Details
```bash
curl -X PUT http://localhost:8080/api/properties/5/images/1 \
  -H "Content-Type: application/json" \
  -d '{
    "imageUrl": "https://example.com/property-image-1-updated.jpg",
    "caption": "Updated living room photo",
    "displayOrder": 1
  }'
```

### Reorder Images
```bash
curl -X POST http://localhost:8080/api/properties/5/images/reorder \
  -H "Content-Type: application/json" \
  -d '[2, 1, 3]'
```

### Delete Image
```bash
curl -X DELETE http://localhost:8080/api/properties/5/images/3
```

---

## 6. Favorites Management

### Add Property to Favorites
```bash
curl -X POST "http://localhost:8080/api/favorites?userId=1&propertyId=5&notes=Beautiful house with great location"
```

### Get User's Favorites (Non-Paginated)
```bash
curl -X GET http://localhost:8080/api/favorites/user/1
```

### Get User's Favorites (Paginated)
```bash
curl -X GET "http://localhost:8080/api/favorites/user/1/paged?page=0&size=10&sortBy=createdAt&direction=DESC"
```

### Check if Property is Favorited
```bash
curl -X GET "http://localhost:8080/api/favorites/check?userId=1&propertyId=5"
```

### Get Favorite Count for Property
```bash
curl -X GET http://localhost:8080/api/favorites/count/5
```

### Toggle Favorite (Add if not, Remove if yes)
```bash
curl -X POST "http://localhost:8080/api/favorites/toggle?userId=1&propertyId=5"
```

### Update Favorite Notes
```bash
curl -X PATCH http://localhost:8080/api/favorites/1/notes?notes="Updated notes about this property"
```

### Remove from Favorites
```bash
curl -X DELETE "http://localhost:8080/api/favorites?userId=1&propertyId=5"
```

### Remove Favorite by ID
```bash
curl -X DELETE http://localhost:8080/api/favorites/1
```

---

## 7. Schedule Viewing

### Schedule a Viewing
```bash
curl -X POST http://localhost:8080/api/schedule-viewings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "propertyId": 5,
    "viewingDate": "2026-02-15",
    "viewingTime": "14:30",
    "notes": "Interested for family home with backyard"
  }'
```

### Get User's Viewings (Non-Paginated)
```bash
curl -X GET http://localhost:8080/api/schedule-viewings/user/1
```

### Get User's Viewings (Paginated)
```bash
curl -X GET "http://localhost:8080/api/schedule-viewings/user/1/paged?page=0&size=10&sortBy=viewingDate&direction=ASC"
```

### Get User's Viewings by Status
```bash
curl -X GET "http://localhost:8080/api/schedule-viewings/user/1/status/PENDING"
```

### Get Property Viewings
```bash
curl -X GET http://localhost:8080/api/schedule-viewings/property/5
```

### Get Property Viewings by Status
```bash
curl -X GET "http://localhost:8080/api/schedule-viewings/property/5/status/CONFIRMED"
```

### Get Viewings for Owner/Agent
```bash
curl -X GET http://localhost:8080/api/schedule-viewings/owner/2
```

### Confirm Viewing (Agent Action)
```bash
curl -X PUT http://localhost:8080/api/schedule-viewings/1/confirm
```

### Reject Viewing (Agent Action)
```bash
curl -X PUT "http://localhost:8080/api/schedule-viewings/1/reject?rejectionReason=Property has already been sold"
```

### Complete Viewing
```bash
curl -X PUT http://localhost:8080/api/schedule-viewings/1/complete
```

### Cancel Viewing
```bash
curl -X PUT http://localhost:8080/api/schedule-viewings/1/cancel
```

### Get Confirmed Viewing Count for Property
```bash
curl -X GET http://localhost:8080/api/schedule-viewings/property/5/confirmed-count
```

### Delete Viewing
```bash
curl -X DELETE http://localhost:8080/api/schedule-viewings/1
```

---

## 8. Complete User Journey Example

### Step 1: Register as Buyer
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Johnson",
    "email": "alice@example.com",
    "password": "secure123",
    "confirmPassword": "secure123",
    "phone": "555-1111111",
    "userType": "BUYER"
  }'
# Save userId from response (e.g., 10)
```

### Step 2: Search for Properties by Builder
```bash
curl -X GET "http://localhost:8080/api/builder-groups"
# Get builderGroupId (e.g., 1)

curl -X GET "http://localhost:8080/api/properties/search?builderGroupId=1&page=0&size=20"
# Get propertyId (e.g., 5)
```

### Step 3: View Property Images
```bash
curl -X GET http://localhost:8080/api/properties/5/images
```

### Step 4: Add to Favorites
```bash
curl -X POST "http://localhost:8080/api/favorites?userId=10&propertyId=5&notes=Love this property!"
```

### Step 5: Schedule a Viewing
```bash
curl -X POST http://localhost:8080/api/schedule-viewings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 10,
    "propertyId": 5,
    "viewingDate": "2026-02-20",
    "viewingTime": "10:00",
    "notes": "Very interested, prefer morning time"
  }'
```

### Step 6: Check Favorites
```bash
curl -X GET "http://localhost:8080/api/favorites/user/10/paged?page=0&size=10"
```

### Step 7: Check Viewings
```bash
curl -X GET "http://localhost:8080/api/schedule-viewings/user/10/paged?page=0&size=10"
```

---

## 9. Error Testing

### Test Password Mismatch
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "password": "password123",
    "confirmPassword": "different123",
    "phone": "555-0000000",
    "userType": "BUYER"
  }'
# Expected: Error - "Passwords do not match"
```

### Test Agent Registration Without License
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "NoLicense",
    "lastName": "Agent",
    "email": "nolicense@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phone": "555-0000000",
    "userType": "AGENT"
  }'
# Expected: Error - "License number is required for agent registration"
```

### Test Invalid License Format
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "BadLicense",
    "lastName": "Agent",
    "email": "badlicense@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phone": "555-0000000",
    "userType": "AGENT",
    "licenseNumber": "invalid-license"
  }'
# Expected: Validation error - "License number must be in format XX-12345"
```

### Test Past Date Viewing
```bash
curl -X POST http://localhost:8080/api/schedule-viewings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "propertyId": 5,
    "viewingDate": "2025-01-01",
    "viewingTime": "10:00",
    "notes": "Past date"
  }'
# Expected: Error - "Viewing date must be in the future"
```

---

## Testing Checklist

- [ ] User Registration (BUYER)
- [ ] User Registration (AGENT with license)
- [ ] Agent Login
- [ ] Create Builder Groups
- [ ] Search by Builder Group
- [ ] Add Images to Property
- [ ] Set Primary Image
- [ ] Reorder Images
- [ ] Add to Favorites
- [ ] View Favorites (Paginated)
- [ ] Toggle Favorite
- [ ] Schedule Viewing
- [ ] View User Viewings (Paginated)
- [ ] Confirm Viewing
- [ ] Reject Viewing
- [ ] Complete Viewing
- [ ] Cancel Viewing
- [ ] Error cases (invalid data)

---

**Last Updated:** January 27, 2026
