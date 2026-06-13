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
    IconButton,
    Switch,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    FormControlLabel,
    Checkbox,
    Snackbar,
    Alert,
    CircularProgress
} from '@mui/material';
import {
    Add as AddIcon,
    Delete as DeleteIcon,
    Refresh as RefreshIcon
} from '@mui/icons-material';
import { supabase } from '../supabaseClient';
import { formatDate } from '../utils/helpers';

const Educations = () => {
    const [educations, setEducations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

    // Form State
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        videoUrl: '',
        pdfUrl: '',
        isPublished: true
    });

    const fetchEducations = async () => {
        setLoading(true);
        try {
            const { data, error } = await supabase
                .from('educations')
                .select('*')
                .order('created_at', { ascending: false });
            if (error) throw error;
            setEducations(data || []);
        } catch (error) {
            console.error("Error fetching educations:", error);
            showSnackbar('Eğitimler yüklenirken bir hata oluştu.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchEducations();
    }, []);

    const handleInputChange = (e) => {
        const { name, value, checked, type } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async () => {
        if (!formData.title) {
            showSnackbar('Başlık alanı zorunludur.', 'error');
            return;
        }

        try {
            const newEdu = {
                title: formData.title,
                description: formData.description,
                video_url: formData.videoUrl || null,
                pdf_url: formData.pdfUrl || null,
                is_published: formData.isPublished,
                created_at: Date.now()
            };

            const { data, error } = await supabase
                .from('educations')
                .insert([newEdu])
                .select();
            if (error) throw error;

            if (data && data.length > 0) {
                setEducations(prev => [data[0], ...prev]);
            } else {
                await fetchEducations();
            }
            setDialogOpen(false);
            setFormData({ title: '', description: '', videoUrl: '', pdfUrl: '', isPublished: true });
            showSnackbar('Eğitim başarıyla eklendi.', 'success');
        } catch (error) {
            console.error("Error adding education:", error);
            showSnackbar('Eğitim eklenirken bir hata oluştu.', 'error');
        }
    };

    const handleTogglePublish = async (eduId, currentStatus) => {
        try {
            const { error } = await supabase
                .from('educations')
                .update({ is_published: !currentStatus })
                .eq('id', eduId);
            if (error) throw error;

            setEducations(prev => prev.map(e => e.id === eduId ? { ...e, is_published: !currentStatus } : e));
            showSnackbar('Yayın durumu güncellendi.', 'success');
        } catch (error) {
            console.error("Error toggling publish:", error);
            showSnackbar('Güncelleme başarısız oldu.', 'error');
        }
    };

    const handleDelete = async (eduId) => {
        if (!window.confirm('Bu eğitimi silmek istediğinize emin misiniz?')) return;

        try {
            const { error } = await supabase
                .from('educations')
                .delete()
                .eq('id', eduId);
            if (error) throw error;

            setEducations(prev => prev.filter(e => e.id !== eduId));
            showSnackbar('Eğitim silindi.', 'info');
        } catch (error) {
            console.error("Error deleting education:", error);
            showSnackbar('Silme işlemi başarısız oldu.', 'error');
        }
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    return (
        <Box sx={{ pb: 4 }}>
            <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                    <Typography variant="h4" fontWeight="800" color="primary.main">
                        Eğitim Yönetimi
                    </Typography>
                    <Typography variant="body1" color="textSecondary">
                        İhracat ve dış ticaret eğitimlerini yönetin
                    </Typography>
                </Box>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <IconButton onClick={fetchEducations} disabled={loading} color="primary">
                        <RefreshIcon />
                    </IconButton>
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={() => setDialogOpen(true)}
                        sx={{ borderRadius: 2 }}
                    >
                        Yeni Eğitim Ekle
                    </Button>
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
                                <TableCell sx={{ fontWeight: 'bold' }}>Eğitim Başlığı</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Yayın Durumu</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Eklenme Tarihi</TableCell>
                                <TableCell align="right" sx={{ fontWeight: 'bold' }}>İşlemler</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {educations.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={4} align="center" sx={{ py: 8 }}>
                                        <Typography color="textSecondary">Henüz eğitim eklenmemiş.</Typography>
                                    </TableCell>
                                </TableRow>
                            ) : (
                                educations.map((edu) => (
                                    <TableRow key={edu.id} hover>
                                        <TableCell sx={{ fontWeight: 500 }}>{edu.title}</TableCell>
                                        <TableCell>
                                            <FormControlLabel
                                                control={
                                                    <Switch
                                                        checked={!!edu.is_published}
                                                        onChange={() => handleTogglePublish(edu.id, !!edu.is_published)}
                                                        color="success"
                                                        size="small"
                                                    />
                                                }
                                                label={edu.is_published ? "Yayında" : "Taslak"}
                                                sx={{ '& .MuiTypography-root': { fontSize: '0.8rem', fontWeight: 600 } }}
                                            />
                                        </TableCell>
                                        <TableCell color="textSecondary">
                                            {edu.created_at ? formatDate(Number(edu.created_at)) : '—'}
                                        </TableCell>
                                        <TableCell align="right">
                                            <IconButton onClick={() => handleDelete(edu.id)} color="error">
                                                <DeleteIcon />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                )}
            </TableContainer>

            {/* Create Dialog */}
            <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} fullWidth maxWidth="sm">
                <DialogTitle sx={{ fontWeight: 'bold' }}>Yeni Eğitim İçeriği Oluştur</DialogTitle>
                <DialogContent>
                    <Box sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 3 }}>
                        <TextField
                            label="Eğitim Başlığı"
                            name="title"
                            value={formData.title}
                            onChange={handleInputChange}
                            fullWidth
                            required
                        />
                        <TextField
                            label="Açıklama"
                            name="description"
                            value={formData.description}
                            onChange={handleInputChange}
                            fullWidth
                            multiline
                            rows={3}
                        />
                        <TextField
                            label="Video URL (YouTube/Vimeo)"
                            name="videoUrl"
                            value={formData.videoUrl}
                            onChange={handleInputChange}
                            fullWidth
                            placeholder="https://..."
                        />
                        <TextField
                            label="PDF Kaynak URL"
                            name="pdfUrl"
                            value={formData.pdfUrl}
                            onChange={handleInputChange}
                            fullWidth
                            placeholder="https://..."
                        />
                        <FormControlLabel
                            control={
                                <Checkbox
                                    name="isPublished"
                                    checked={formData.isPublished}
                                    onChange={handleInputChange}
                                />
                            }
                            label="Hemen Yayınla"
                        />
                    </Box>
                </DialogContent>
                <DialogActions sx={{ p: 3 }}>
                    <Button onClick={() => setDialogOpen(false)}>İptal</Button>
                    <Button onClick={handleSubmit} variant="contained" sx={{ borderRadius: 2 }}>
                        Eğitimi Kaydet
                    </Button>
                </DialogActions>
            </Dialog>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={4000}
                onClose={() => setSnackbar(prev => ({ ...prev, open: false }))}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
            >
                <Alert severity={snackbar.severity} sx={{ width: '100%', borderRadius: 3 }}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Educations;
