iwr "http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000" | Select-Object -ExpandProperty Contentiwr "http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000" | Select-Object -ExpandProperty Contentgit push origin master# ⚡ Quick Reference Card - Recommend API Testing

## Your PowerShell Command Error Fixed

### ❌ What You Typed (Wrong)
```powershell
curl GET "http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000"
```

**Error**: Positional parameter not recognized

### ✅ What You Should Type (Correct)

**OPTION 1 - PowerShell Native (Best for PowerShell):**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000" -Method Get
```

**OPTION 2 - curl.exe (Traditional curl syntax):**
```powershell
curl.exe -X GET "http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000"
```

**OPTION 3 - Browser (Easiest):**
```
http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000
```

**OPTION 4 - Short PowerShell version:**
```powershell
iwr "http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000"
```

---

## Before Testing: Start the App

```powershell
cd "D:\CDAC Project\Atharva\Atharva\real-estate-backend"
mvn clean spring-boot:run
```

Wait for:
```
Tomcat started on port(s): 8080
```

---

## Test Commands by Tool

### PowerShell
```powershell
iwr "http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000" | Select-Object -ExpandProperty Content
```

### cmd.exe (Command Prompt)
```cmd
curl.exe -X GET "http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000"
```

### Browser
```
Open: http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000
```

### Postman
```
GET http://localhost:8080/api/properties/recommendations
Query Params: location=Mumbai, budget=5000000
```

---

## Expected Responses

### Success (Has Data)
```json
{"success":true,"data":[{...properties...}],"message":null}
Status: 200 OK
```

### Success (No Match)
```json
{"success":true,"data":[],"message":null}
Status: 200 OK
```

### Error (App Not Running)
```
Connection refused
Status: Connection Error
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Positional parameter not found" | Use: `iwr "url"` or `curl.exe -X GET "url"` |
| Connection refused | Run: `mvn clean spring-boot:run` |
| Empty results | Check database has properties |
| Timeout | App taking too long to respond |

---

## Test Different Locations

```powershell
# Mumbai
iwr "http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000" | Select-Object -ExpandProperty Content

# Bangalore
iwr "http://localhost:8080/api/properties/recommendations?location=Bangalore&budget=2000000" | Select-Object -ExpandProperty Content

# Delhi
iwr "http://localhost:8080/api/properties/recommendations?location=Delhi&budget=10000000" | Select-Object -ExpandProperty Content
```

---

## Check Application Status

```powershell
# Health check
curl.exe http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}
```

---

## Save to Favorites

**Most Used Command for PowerShell:**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/properties/recommendations?location=Mumbai&budget=5000000" -Method Get | Select-Object -ExpandProperty Content | ConvertFrom-Json | ConvertTo-Json
```

---

**Status: ✅ Ready to Test**

See: START_AND_TEST_RECOMMEND_API.md for detailed guide
