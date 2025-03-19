import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import MoodTrackerService from './mood-tracker.service';
import { useDateFormat } from '@/shared/composables';
import { type IMoodTracker } from '@/shared/model/mood-tracker.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MoodTrackerDetails',
  setup() {
    const dateFormat = useDateFormat();
    const moodTrackerService = inject('moodTrackerService', () => new MoodTrackerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const moodTracker: Ref<IMoodTracker> = ref({});

    const retrieveMoodTracker = async moodTrackerId => {
      try {
        const res = await moodTrackerService().find(moodTrackerId);
        moodTracker.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.moodTrackerId) {
      retrieveMoodTracker(route.params.moodTrackerId);
    }

    return {
      ...dateFormat,
      alertService,
      moodTracker,

      previousState,
    };
  },
});
