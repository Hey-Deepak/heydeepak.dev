# Security Policy for heydeepak.dev

## 🔒 Secret Management

This is a **public, open-source repository**. Protect all sensitive information:

### ✅ NEVER Commit to Git

- **API Keys** (Gemini, Claude, Sarvam, etc.)
- **Private tokens** (GitHub, Vercel, etc.)
- **Database credentials**
- **SSH keys**
- **Personal information**
- **Environment-specific configs**

### 📋 Files Protected by `.gitignore`

```
.env              # Environment variables (local)
.env.local        # Environment variables (personal)
local.properties  # Gradle local properties
.vercel/          # Vercel configuration
```

### 🔑 How to Use API Keys Safely

**1. Local Development:**
```bash
# Create .env.local (NOT committed to git)
DAEMON_API_KEY=your_actual_key_here
GEMINI_API_KEY=your_actual_key_here
```

**2. Load in Code:**
```kotlin
// ✅ Good: Load from environment
val apiKey = System.getenv("DAEMON_API_KEY") ?: ""

// ❌ Bad: Hardcode in code
val apiKey = "sk_live_abc123..."  // NEVER DO THIS
```

**3. In Vercel (Production):**
- Go to Vercel Dashboard → Project Settings → Environment Variables
- Add secrets there (NOT in code or vercel.json)
- Vercel injects them at build/runtime

### 🛡️ For Contributors

1. **Clone the repo:**
   ```bash
   git clone https://github.com/Hey-Deepak/heydeepak.dev.git
   ```

2. **Create your own `.env.local`:**
   ```bash
   cp .env.example .env.local
   # Edit .env.local with YOUR keys
   ```

3. **Verify `.env.local` is in `.gitignore`:**
   ```bash
   git check-ignore .env.local  # Should return: .env.local
   ```

4. **Commit code, NOT secrets:**
   ```bash
   git add .
   git commit -m "feat: add feature"
   # .env.local automatically excluded ✓
   ```

### 🚨 If You Accidentally Commit a Secret

1. **Immediately revoke the key/token** in your service dashboard
2. **Remove from git history:**
   ```bash
   git rm --cached .env.local
   git commit -m "Remove .env.local"
   git push
   ```
3. **Create a new key** with restricted scopes
4. **Report** if it was a shared secret (e.g., in a team project)

### ✅ Current Portfolio Status

This portfolio **does NOT currently use any API keys** because:
- It's a static website (no backend)
- All content is hardcoded/immutable
- No authentication needed
- No external service calls

**If we add features that need keys (e.g., contact form, analytics):**
- Add to `.env.example` first
- Document in this file
- Load from `System.getenv()` or environment variables
- Never hardcode

### 📚 References

- [GitHub: Removing sensitive data](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/removing-sensitive-data-from-a-repository)
- [12 Factor App: Config](https://12factor.net/config)
- [OWASP: Secrets Management](https://owasp.org/www-community/Source_Code_Disclosure)

---

**Rule:** If it can authenticate or authorize → it's a secret → it goes in `.env.local`, NOT in code.
