import React, { useState, useEffect } from 'react';
import { Grid, Paper, Typography, Box, Card, CardContent, Skeleton } from '@mui/material';
import {
    CorporateFare,
    HowToReg,
    Handshake,
    Storefront,
    School,
    TrendingUp,
    Notifications
} from '@mui/icons-material';
import { supabase } from '../supabaseClient';

const StatCard = ({ title, value, icon, color, loading }) => (
    <Card sx={{ height: '100%', transition: 'transform 0.2s', '&:hover': { transform: 'translateY(-4px)' } }}>
        <CardContent sx={{ display: 'flex', alignItems: 'center', p: 3 }}>
            <Box sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                p: 2,
                borderRadius: 4,
                backgroundColor: `${color}12`,
                color: color,
                mr: 3,
                boxShadow: `0 8px 16px -4px ${color}33`
            }}>
                {icon}
            </Box>
            <Box sx={{ flexGrow: 1 }}>
                <Typography variant="body2" color="textSecondary" fontWeight="600" sx={{ mb: 0.5, letterSpacing: 0.5 }}>
                    {title.toUpperCase()}
                </Typography>
                {loading ? (
                    <Skeleton width="60%" height={40} />
                ) : (
                    <Typography variant="h4" fontWeight="800" color="text.primary">
                        {value.toLocaleString()}
                    </Typography>
                )}
            </Box>
        </CardContent>
    </Card>
);

const Dashboard = () => {
    const [stats, setStats] = useState({
        firms: 0,
        pendingUsers: 0,
        activeMatches: 0,
        activeMarketplaces: 0,
        publishedTrainings: 0
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchStats = async () => {
            setLoading(true);
            try {
                const [
                    { count: firmsCount },
                    { count: pendingCount },
                    { count: matchesCount },
                    { count: marketplacesCount },
                    { count: educationsCount }
                ] = await Promise.all([
                    supabase.from('companies').select('*', { count: 'exact', head: true }),
                    supabase.from('profiles').select('*', { count: 'exact', head: true }).eq('role', 'GUEST'), // Guest role in Android app represents newly registered users waiting for approval
                    supabase.from('b2b_matches').select('*', { count: 'exact', head: true }),
                    supabase.from('marketplace_connections').select('*', { count: 'exact', head: true }).eq('status', 'connected'),
                    supabase.from('educations').select('*', { count: 'exact', head: true }).eq('is_published', true)
                ]);

                setStats({
                    firms: firmsCount || 0,
                    pendingUsers: pendingCount || 0,
                    activeMatches: matchesCount || 0,
                    activeMarketplaces: marketplacesCount || 0,
                    publishedTrainings: educationsCount || 0
                });
            } catch (error) {
                console.error("Error fetching dashboard stats:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchStats();
    }, []);

    return (
        <Box sx={{ pb: 4 }}>
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" fontWeight="800" color="primary.main">
                    Yönetim Paneli
                </Typography>
                <Typography variant="body1" color="textSecondary">
                    Sistem genelindeki güncel veriler ve istatistikler
                </Typography>
            </Box>

            <Grid container spacing={3}>
                <Grid item xs={12} sm={6} md={4} lg={2.4}>
                    <StatCard
                        title="Kayıtlı Firmalar"
                        value={stats.firms}
                        icon={<CorporateFare fontSize="large" />}
                        color="#1B263B"
                        loading={loading}
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4} lg={2.4}>
                    <StatCard
                        title="Bekleyen Onaylar"
                        value={stats.pendingUsers}
                        icon={<HowToReg fontSize="large" />}
                        color="#FF9800"
                        loading={loading}
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4} lg={2.4}>
                    <StatCard
                        title="B2B Eşleşmeleri"
                        value={stats.activeMatches}
                        icon={<Handshake fontSize="large" />}
                        color="#2E7D32"
                        loading={loading}
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4} lg={2.4}>
                    <StatCard
                        title="Aktif Entegrasyonlar"
                        value={stats.activeMarketplaces}
                        icon={<Storefront fontSize="large" />}
                        color="#0288D1"
                        loading={loading}
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4} lg={2.4}>
                    <StatCard
                        title="Yayındaki Eğitimler"
                        value={stats.publishedTrainings}
                        icon={<School fontSize="large" />}
                        color="#7B1FA2"
                        loading={loading}
                    />
                </Grid>

                <Grid item xs={12} md={8}>
                    <Paper sx={{ p: 4, height: 450, borderRadius: 4, display: 'flex', flexDirection: 'column', boxShadow: '0 4px 20px rgba(0,0,0,0.05)' }}>
                        <Box sx={{ mb: 2, display: 'flex', alignItems: 'center' }}>
                            <TrendingUp sx={{ mr: 1, color: 'primary.main' }} />
                            <Typography variant="h6" fontWeight="bold">Sistem Aktivite Analizi</Typography>
                        </Box>
                        <Box sx={{ flexGrow: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', border: '2px dashed #EEE', borderRadius: 2 }}>
                            <Typography color="textSecondary">Grafik Verileri Yükleniyor...</Typography>
                        </Box>
                    </Paper>
                </Grid>

                <Grid item xs={12} md={4}>
                    <Paper sx={{ p: 4, height: 450, borderRadius: 4, display: 'flex', flexDirection: 'column', boxShadow: '0 4px 20px rgba(0,0,0,0.05)' }}>
                        <Box sx={{ mb: 2, display: 'flex', alignItems: 'center' }}>
                            <Notifications sx={{ mr: 1, color: 'primary.main' }} />
                            <Typography variant="h6" fontWeight="bold">Son Bildirimler</Typography>
                        </Box>
                        <Box sx={{ flexGrow: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', border: '1px solid #F5F5F5', borderRadius: 2 }}>
                            <Typography color="textSecondary" variant="body2">Henüz yeni bir bildirim yok.</Typography>
                        </Box>
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Dashboard;
