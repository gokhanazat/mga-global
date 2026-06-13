import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Button,
    Chip,
    Snackbar,
    Alert,
    CircularProgress,
    IconButton,
    Tooltip
} from '@mui/material';
import {
    CheckCircle as ApproveIcon,
    Cancel as RejectIcon,
    Refresh as RefreshIcon
} from '@mui/icons-material';
import { supabase } from '../supabaseClient';
import { formatDate } from '../utils/helpers';

const Users = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
    const [actionLoading, setActionLoading] = useState(null);

    const fetchPendingUsers = async () => {
        setLoading(true);
        try {
            // Fetch profiles with role GUEST (pending approval)
            const { data, error } = await supabase
                .from('profiles')
                .select('*')
                .eq('role', 'GUEST');

            if (error) throw error;

            const userList = (data || []).map(p => ({
                id: p.id,
                email: p.email,
                createdAt: Number(p.created_at) || Date.now(),
                role: p.role,
                status: 'pending'
            }));

            // Sort by createdAt descending
            userList.sort((a, b) => b.createdAt - a.createdAt);
            setUsers(userList);
        } catch (error) {
            console.error("Error fetching pending users:", error);
            showSnackbar('Kullanıcılar yüklenirken bir hata oluştu.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchPendingUsers();
    }, []);

    const handleAction = async (userId, action) => {
        setActionLoading(userId);
        try {
            if (action === 'approve') {
                const { error } = await supabase
                    .from('profiles')
                    .update({
                        role: 'MEMBER'
                    })
                    .eq('id', userId);

                if (error) throw error;
                showSnackbar('Kullanıcı başarıyla onaylandı ve "MEMBER" rolü atandı.', 'success');
            } else {
                // Delete user profile on reject
                const { error } = await supabase
                    .from('profiles')
                    .delete()
                    .eq('id', userId);

                if (error) throw error;
                showSnackbar('Kullanıcı başvurusu reddedildi.', 'warning');
            }
            // Remove from local state
            setUsers(prev => prev.filter(u => u.id !== userId));
        } catch (error) {
            console.error(`Error during ${action}:`, error);
            showSnackbar('İşlem sırasında bir hata oluştu.', 'error');
        } finally {
            setActionLoading(null);
        }
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleCloseSnackbar = () => {
        setSnackbar(prev => ({ ...prev, open: false }));
    };

    return (
        <Box sx={{ pb: 4 }}>
            <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                    <Typography variant="h4" fontWeight="800" color="primary.main">
                        Kullanıcı Onayları
                    </Typography>
                    <Typography variant="body1" color="textSecondary">
                        Onay bekleyen (GUEST) üye hesaplarını yönetin
                    </Typography>
                </Box>
                <Tooltip title="Listeyi Yenile">
                    <IconButton onClick={fetchPendingUsers} disabled={loading} color="primary">
                        <RefreshIcon />
                    </IconButton>
                </Tooltip>
            </Box>

            <TableContainer component={Paper} sx={{ borderRadius: 4, boxShadow: '0 4px 20px rgba(0,0,0,0.05)' }}>
                {loading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', p: 8 }}>
                        <CircularProgress />
                    </Box>
                ) : (
                    <Table sx={{ minWidth: 650 }}>
                        <TableHead sx={{ backgroundColor: '#F8F9FA' }}>
                            <TableRow>
                                <TableCell sx={{ fontWeight: 'bold' }}>E-posta</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Kayıt Tarihi</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Mevcut Rol</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Durum</TableCell>
                                <TableCell align="right" sx={{ fontWeight: 'bold' }}>İşlemler</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {users.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5} align="center" sx={{ py: 8 }}>
                                        <Typography color="textSecondary">Onay bekleyen kullanıcı bulunamadı.</Typography>
                                    </TableCell>
                                </TableRow>
                            ) : (
                                users.map((user) => (
                                    <TableRow key={user.id} hover>
                                        <TableCell sx={{ fontWeight: 500 }}>{user.email}</TableCell>
                                        <TableCell color="textSecondary">
                                            {user.createdAt ? formatDate(user.createdAt) : '—'}
                                        </TableCell>
                                        <TableCell>
                                            <Chip
                                                label={user.role}
                                                size="small"
                                                variant="outlined"
                                                sx={{ borderRadius: 1 }}
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <Chip
                                                label="BEKLEYEN"
                                                size="small"
                                                color="warning"
                                                sx={{ fontWeight: 'bold', fontSize: '0.65rem' }}
                                            />
                                        </TableCell>
                                        <TableCell align="right">
                                            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                                                <Button
                                                    variant="contained"
                                                    color="success"
                                                    size="small"
                                                    startIcon={<ApproveIcon />}
                                                    onClick={() => handleAction(user.id, 'approve')}
                                                    disabled={actionLoading === user.id}
                                                    sx={{ borderRadius: 2 }}
                                                >
                                                    Onayla
                                                </Button>
                                                <Button
                                                    variant="outlined"
                                                    color="error"
                                                    size="small"
                                                    startIcon={<RejectIcon />}
                                                    onClick={() => handleAction(user.id, 'reject')}
                                                    disabled={actionLoading === user.id}
                                                    sx={{ borderRadius: 2 }}
                                                >
                                                    Reddet
                                                </Button>
                                            </Box>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                )}
            </TableContainer>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={handleCloseSnackbar}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
            >
                <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: '100%', borderRadius: 3 }}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Users;
