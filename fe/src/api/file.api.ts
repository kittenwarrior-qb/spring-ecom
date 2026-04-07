import adminApiClient from '@/lib/admin-api-client'
import type { ApiResponse, FileUploadResponse, FileListResponse } from '@/types/api'

const FILE_BASE_URL = '/api/files'

export const fileApi = {
  // Upload file to MinIO with presigned URL
  upload: async (file: File): Promise<FileUploadResponse> => {
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await adminApiClient.post<ApiResponse<FileUploadResponse>>(
      `${FILE_BASE_URL}/upload/presigned`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    )
    return response.data.data
  },

  // List all files with presigned URLs
  list: async (): Promise<FileListResponse> => {
    const response = await adminApiClient.get<ApiResponse<FileListResponse>>(`${FILE_BASE_URL}/list`)
    return response.data.data
  },

  // Delete file
  delete: async (filename: string): Promise<void> => {
    await adminApiClient.delete<ApiResponse<void>>(`${FILE_BASE_URL}/delete/${filename}`)
  },
}
