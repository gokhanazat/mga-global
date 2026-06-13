import React, { useState } from 'react';
import {
    Box,
    Drawer,
    AppBar,
    Toolbar,
    List,
    Typography,
    Divider,
    IconButton,
    ListItem,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Avatar,
    Menu,
    MenuItem
} from '@mui/material';
import {
    Menu as MenuIcon,
    ChevronLeft as ChevronLeftIcon,
    Dashboard as DashboardIcon,
    People as PeopleIcon,
    Settings as SettingsIcon,
    ExitToApp as LogoutIcon,
    Store as StoreIcon,
    Handshake as B2BIcon,
    School as SchoolIcon,
    AdminPanelSettings as RolesIcon
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';

import { useAuth } from '../hooks/useAuth';
import { supabase } from '../supabaseClient';

const drawerWidth = 260;

const DashboardLayout = ({ children }) => {
    const [open, setOpen] = useState(true);
    const [anchorEl, setAnchorEl] = useState(null);
    const navigate = useNavigate();
    const location = useLocation();
    const { user } = useAuth();

    const handleDrawerToggle = () => {
        setOpen(!open);
    };

    const handleMenuOpen = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleMenuClose = () => {
        setAnchorEl(null);
    };

    const handleLogout = async () => {
        try {
            await supabase.auth.signOut();
            navigate('/login');
        } catch (error) {
            console.error("Error signing out:", error);
        }
    };

    const menuItems = [
        { text: 'Kontrol Paneli', icon: <DashboardIcon />, path: '/' },
        { text: 'Firma Yönetimi', icon: <StoreIcon />, path: '/firms' },
        { text: 'B2B Eşleşmeleri', icon: <B2BIcon />, path: '/b2b-matches' },
        { text: 'Marketplaces', icon: <StoreIcon />, path: '/marketplaces' },
        { text: 'Eğitim Yönetimi', icon: <SchoolIcon />, path: '/educations' },
        { text: 'Kullanıcı Onayları', icon: <PeopleIcon />, path: '/users' },
        { text: 'Rol & Yetkiler', icon: <RolesIcon />, path: '/roles' },
        { text: 'Ayarlar', icon: <SettingsIcon />, path: '/settings' },
    ];

    return (
        <Box sx={{ display: 'flex' }}>
            <AppBar
                position="fixed"
                sx={{
                    zIndex: (theme) => theme.zIndex.drawer + 1,
                    transition: (theme) => theme.transitions.create(['width', 'margin'], {
                        easing: theme.transitions.easing.sharp,
                        duration: theme.transitions.duration.leavingScreen,
                    }),
                    ...(open && {
                        marginLeft: drawerWidth,
                        width: `calc(100% - ${drawerWidth}px)`,
                        transition: (theme) => theme.transitions.create(['width', 'margin'], {
                            easing: theme.transitions.easing.sharp,
                            duration: theme.transitions.duration.enteringScreen,
                        }),
                    }),
                    backgroundColor: 'background.paper',
                    color: 'text.primary',
                    boxShadow: 'none',
                    borderBottom: '1px solid #E0E0E0'
                }}
            >
                <Toolbar>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        onClick={handleDrawerToggle}
                        edge="start"
                        sx={{ marginRight: 2 }}
                    >
                        {open ? <ChevronLeftIcon /> : <MenuIcon />}
                    </IconButton>
                    <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1, fontWeight: 'bold' }}>
                        {menuItems.find(item => item.path === location.pathname)?.text || 'Admin Panel'}
                    </Typography>

                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Typography variant="body2" sx={{ mr: 2, display: { xs: 'none', sm: 'block' }, fontWeight: 500 }}>
                            {user?.email || 'Admin User'}
                        </Typography>
                        <IconButton onClick={handleMenuOpen} sx={{ p: 0 }}>
                            <Avatar sx={{ bgcolor: 'primary.main', fontSize: '0.9rem' }}>
                                {user?.email?.substring(0, 2).toUpperCase() || 'AD'}
                            </Avatar>
                        </IconButton>
                        <Menu
                            anchorEl={anchorEl}
                            open={Boolean(anchorEl)}
                            onClose={handleMenuClose}
                            sx={{ mt: 1 }}
                        >
                            <MenuItem onClick={handleMenuClose}>Profile</MenuItem>
                            <MenuItem onClick={handleLogout}>
                                <ListItemIcon>
                                    <LogoutIcon fontSize="small" />
                                </ListItemIcon>
                                Logout
                            </MenuItem>
                        </Menu>
                    </Box>
                </Toolbar>
            </AppBar>

            <Drawer
                variant="permanent"
                open={open}
                sx={{
                    width: open ? drawerWidth : 72,
                    flexShrink: 0,
                    whiteSpace: 'nowrap',
                    boxSizing: 'border-box',
                    '& .MuiDrawer-paper': {
                        width: open ? drawerWidth : 72,
                        transition: (theme) => theme.transitions.create('width', {
                            easing: theme.transitions.easing.sharp,
                            duration: theme.transitions.duration.enteringScreen,
                        }),
                        overflowX: 'hidden',
                        backgroundColor: 'primary.main',
                        color: 'white',
                        borderRight: 'none'
                    },
                }}
            >
                <Toolbar sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', px: [1] }}>
                    <Typography variant="h5" sx={{ fontWeight: 'bold', color: 'white', py: 2 }}>
                        {open ? 'MGA GLOBAL' : 'MGA'}
                    </Typography>
                </Toolbar>
                <Divider sx={{ backgroundColor: 'rgba(255,255,255,0.1)' }} />
                <List sx={{ mt: 2 }}>
                    {menuItems.map((item) => (
                        <ListItem key={item.text} disablePadding sx={{ display: 'block' }}>
                            <ListItemButton
                                onClick={() => navigate(item.path)}
                                sx={{
                                    minHeight: 48,
                                    justifyContent: open ? 'initial' : 'center',
                                    px: 2.5,
                                    backgroundColor: location.pathname === item.path ? 'rgba(255,255,255,0.1)' : 'transparent',
                                    '&:hover': {
                                        backgroundColor: 'rgba(255,255,255,0.05)',
                                    },
                                }}
                            >
                                <ListItemIcon
                                    sx={{
                                        minWidth: 0,
                                        mr: open ? 3 : 'auto',
                                        justifyContent: 'center',
                                        color: 'inherit'
                                    }}
                                >
                                    {item.icon}
                                </ListItemIcon>
                                <ListItemText primary={item.text} sx={{ opacity: open ? 1 : 0 }} />
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
            </Drawer>

            <Box component="main" sx={{ flexGrow: 1, p: 3, mt: 8, minHeight: '100vh', backgroundColor: 'background.default' }}>
                {children}
            </Box>
        </Box>
    );
};

export default DashboardLayout;
