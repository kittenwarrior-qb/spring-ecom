import { useState, useEffect } from 'react'

export interface Province {
  code: string
  name: string
  name_en: string
  full_name: string
  full_name_en: string
  code_name: string
}

export interface District {
  code: string
  name: string
  name_en: string
  full_name: string
  full_name_en: string
  code_name: string
  province_code: string
}

export interface Ward {
  code: string
  name: string
  name_en: string
  full_name: string
  full_name_en: string
  code_name: string
  district_code: string
}

const API_BASE_URL = 'https://provinces.open-api.vn/api'

export function useVietnamAddress() {
  const [provinces, setProvinces] = useState<Province[]>([])
  const [districts, setDistricts] = useState<District[]>([])
  const [wards, setWards] = useState<Ward[]>([])
  const [loading, setLoading] = useState({
    provinces: false,
    districts: false,
    wards: false,
  })

  // Fetch provinces on mount
  useEffect(() => {
    fetchProvinces()
  }, [])

  const fetchProvinces = async () => {
    try {
      setLoading(prev => ({ ...prev, provinces: true }))
      const response = await fetch(`${API_BASE_URL}/p/`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const data = await response.json()
      setProvinces(Array.isArray(data) ? data : [])
    } catch (error) {
      console.error('Failed to fetch provinces:', error)
      setProvinces([])
    } finally {
      setLoading(prev => ({ ...prev, provinces: false }))
    }
  }

  const fetchDistricts = async (provinceCode: string) => {
    if (!provinceCode) return
    
    try {
      setLoading(prev => ({ ...prev, districts: true }))
      setDistricts([]) // Clear current districts
      setWards([]) // Clear current wards
      
      const response = await fetch(`${API_BASE_URL}/p/${provinceCode}?depth=2`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const data = await response.json()
      setDistricts(Array.isArray(data.districts) ? data.districts : [])
    } catch (error) {
      console.error('Failed to fetch districts:', error)
      setDistricts([])
    } finally {
      setLoading(prev => ({ ...prev, districts: false }))
    }
  }

  const fetchWards = async (districtCode: string) => {
    if (!districtCode) return
    
    try {
      setLoading(prev => ({ ...prev, wards: true }))
      setWards([]) // Clear current wards
      
      const response = await fetch(`${API_BASE_URL}/d/${districtCode}?depth=2`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const data = await response.json()
      setWards(Array.isArray(data.wards) ? data.wards : [])
    } catch (error) {
      
      setWards([])
    } finally {
      setLoading(prev => ({ ...prev, wards: false }))
    }
  }

  return {
    provinces,
    districts,
    wards,
    loading,
    fetchDistricts,
    fetchWards,
  }
}