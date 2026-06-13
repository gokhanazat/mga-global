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
    TextField,
    MenuItem,
    Chip,
    CircularProgress,
    IconButton,
    InputAdornment
} from '@mui/material';
import {
    Refresh as RefreshIcon,
    Search as SearchIcon,
    FilterList as FilterIcon
} from '@mui/icons-material';
import { supabase } from '../supabaseClient';
import { formatDate } from '../utils/helpers';

const Marketplaces = () => {
    const [connections, setConnections] = useState([]);
    const [filteredConnections, setFilteredConnections] = useState([]);
    const [loading, setLoading] = useState(true);
    const [platformFilter, setPlatformFilter] = useState('All');
    const [searchTerm, setSearchTerm] = useState('');

    const platforms = ['All', 'Amazon', 'eBay', 'Shopify', 'WooCommerce', 'Etsy'];

    const fetchConnections = async () => {
        setLoading(true);
        try {
            const { data, error } = await supabase
                .from('marketplace_connections')
                .select('*')
                .order('last_sync', { ascending: false });
            if (error) throw error;
            setConnections(data || []);
            setFilteredConnections(data || []);
        } catch (error) {
            console.error("Error fetching marketplace connections:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchConnections();
    }, []);

    useEffect(() => {
        let result = connections;

        if (platformFilter !== 'All') {
            result = result.filter(c => c.platform?.toLowerCase() === platformFilter.toLowerCase());
        }

        if (searchTerm) {
            result = result.filter(c =>
                c.firm_id?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                c.platform?.toLowerCase().includes(searchTerm.toLowerCase())
            );
        }

        setFilteredConnections(result);
    }, [platformFilter, searchTerm, connections]);

    const getStatusColor = (status) => {
        switch (status?.toLowerCase()) {
            case 'connected': return 'success';
            case 'disconnected': return 'error';
            case 'pending': return 'warning';
            default: return 'default';
        }
    };

    return (
        <Box sx={{ pb: 4 }}>
            <Box sx={{ mb: 4, display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center', gap: 2 }}>
                <Box>
                    <Typography variant="h4" fontWeight="800" color="primary.main">
                        Pazar Yeri Bağlantıları
                    </Typography>
                    <Typography variant="body1" color="textSecondary">
                        Firmaların e-ticaret platformu entegrasyonlarını izleyin
                    </Typography>
                </Box>

                <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                    <TextField
                        select
                        size="small"
                        label="Platform"
                        value={platformFilter}
                        onChange={(e) => setPlatformFilter(e.target.value)}
                        sx={{ minWidth: 150 }}
                        InputProps={{
                            startAdornment: (
                                <InputAdornment position="start">
                                    <FilterIcon fontSize="small" color="action" />
                                </InputAdornment>
                            ),
                        }}
                    >
                        {platforms.map(p => (
                            <MenuItem key={p} value={p}>{p}</MenuItem>
                        ))}
                    </TextField>

                    <TextField
                        size="small"
                        placeholder="Firma ID veya Platform ara..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        InputProps={{
                            startAdornment: (
                                <InputAdornment position="start">
                                    <SearchIcon fontSize="small" color="action" />
                                </InputAdornment>
                            ),
                        }}
                        sx={{ minWidth: 250 }}
                    />

                    <IconButton onClick={fetchConnections} disabled={loading} color="primary">
                        <RefreshIcon />
                    </IconButton>
                </Box>
            </Box>

            <TableContainer component={Paper} sx={{ borderRadius: 4, boxShadow: '0 4px 20px rgba(0,0,0,0.05)' }}>
                {loading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', p: 8 }}>
                        <CircularProgress />
                    </Box>
                ) : (
                    <Table>
                        <TableHead sx={{ backgroundColor: '#F8F9FA' }}>
                            <TableRow>
                                <TableCell sx={{ fontWeight: 'bold' }}>Firma ID</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Platform</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Durum</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Son Senkronizasyon</TableCell>
                                <TableCell align="right" sx={{ fontWeight: 'bold' }}>İşlem</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {filteredConnections.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5} align="center" sx={{ py: 8 }}>
                                        <Typography color="textSecondary">Eşleşen bağlantı bulunamadı.</Typography>
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filteredConnections.map((conn) => (
                                    <TableRow key={conn.id} hover>
                                        <TableCell sx={{ fontWeight: 500, fontFamily: 'monospace' }}>
                                            {conn.firm_id}
                                        </TableCell>
                                        <TableCell>
                                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                                <Typography variant="body2" fontWeight="600">
                                                    {conn.platform}
                                                </Typography>
                                            </Box>
                                        </TableCell>
                                        <TableCell>
                                            <Chip
                                                label={(conn.status || 'unknown').toUpperCase()}
                                                color={getStatusColor(conn.status)}
                                                size="small"
                                                sx={{ fontWeight: 'bold', fontSize: '0.65rem' }}
                                            />
                                        </TableCell>
                                        <TableCell color="textSecondary">
                                            {conn.last_sync ? formatDate(Number(conn.last_sync)) : 'Senkronizasyon yok'}
                                        </TableCell>
                                        <TableCell align="right">
                                            <Chip label="Sadece Oku" size="small" variant="outlined" disabled />
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                )}
            </TableContainer>
        </Box>
    );
};

export default Marketplaces;
