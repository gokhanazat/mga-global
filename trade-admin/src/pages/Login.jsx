import React, { useState } from 'react';
import {
    Box,
    Container,
    Paper,
    Typography,
    TextField,
    Button,
    InputAdornment,
    IconButton,
    Alert
} from '@mui/material';
import { Email, Lock, Visibility, VisibilityOff } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { supabase } from '../supabaseClient';

const Login = () => {
    const [showPassword, setShowPassword] = useState(false);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            // 1. Authenticate with Supabase Auth
            const { data, error: authError } = await supabase.auth.signInWithPassword({
                email,
                password
            });
            if (authError) throw authError;

            const user = data.user;

            // 2. Check user role in profiles table
            const { data: profile, error: profileError } = await supabase
                .from('profiles')
                .select('role')
                .eq('id', user.id)
                .single();

            if (profileError) {
                await supabase.auth.signOut();
                throw new Error("Kullanıcı rolü doğrulanamadı.");
            }

            if (profile) {
                const userRole = (profile.role || '').toUpperCase();

                if (userRole === 'ADMIN') {
                    // Success: User is an admin
                    navigate('/');
                } else {
                    // Failure: Logged in but not an admin
                    await supabase.auth.signOut();
                    setError('Access Denied: You do not have administrator privileges.');
                }
            } else {
                await supabase.auth.signOut();
                setError('User profile not found. Please contact support.');
            }
        } catch (err) {
            console.error('Login error:', err);
            setError(err.message || 'Login failed. Please check your credentials.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Box sx={{
            minHeight: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            backgroundColor: '#F1F3F5'
        }}>
            <Container maxWidth="xs">
                <Paper elevation={0} sx={{ p: 5, borderRadius: 4, textAlign: 'center' }}>
                    <Typography variant="h4" fontWeight="bold" color="primary" sx={{ mb: 1 }}>
                        MGA GLOBAL
                    </Typography>
                    <Typography variant="body2" color="textSecondary" sx={{ mb: 4 }}>
                        Admin Control Center
                    </Typography>

                    {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

                    <form onSubmit={handleLogin}>
                        <TextField
                            fullWidth
                            label="Email Address"
                            variant="outlined"
                            margin="normal"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            disabled={isLoading}
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <Email color="action" />
                                    </InputAdornment>
                                ),
                            }}
                        />
                        <TextField
                            fullWidth
                            label="Password"
                            variant="outlined"
                            margin="normal"
                            type={showPassword ? 'text' : 'password'}
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            disabled={isLoading}
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <Lock color="action" />
                                    </InputAdornment>
                                ),
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <IconButton onClick={() => setShowPassword(!showPassword)}>
                                            {showPassword ? <VisibilityOff /> : <Visibility />}
                                        </IconButton>
                                    </InputAdornment>
                                ),
                            }}
                        />

                        <Button
                            fullWidth
                            variant="contained"
                            size="large"
                            type="submit"
                            disabled={isLoading}
                            sx={{ mt: 4, py: 1.5, fontWeight: 'bold' }}
                        >
                            {isLoading ? 'Authenticating...' : 'Log In'}
                        </Button>
                    </form>

                    <Typography variant="body2" color="textSecondary" sx={{ mt: 4 }}>
                        &copy; 2026 MGA GLOBAL Enterprise
                    </Typography>
                </Paper>
            </Container>
        </Box>
    );
};

export default Login;
